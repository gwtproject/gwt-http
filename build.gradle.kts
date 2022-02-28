import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    id("local.maven-publish")
    alias(libs.plugins.errorprone)
    alias(libs.plugins.spotless)
}

buildscript {
    dependencyLocking {
        lockAllConfigurations()
        lockMode.set(LockMode.STRICT)
    }
}
dependencyLocking {
    lockAllConfigurations()
    lockMode.set(LockMode.STRICT)
}

group = "org.gwtproject.http"

repositories {
    mavenCentral()
}

dependencies {
    errorprone(libs.errorprone.core)
    errorproneJavac(libs.errorprone.javac)

    implementation(libs.elemental2.dom)
    implementation(libs.elemental2.core)
    implementation(libs.jsinterop.base)

    testImplementation(libs.junit)
    testImplementation(libs.gwt.user)
    testImplementation(libs.gwt.dev)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Werror", "-Xlint:all"))
    if (JavaVersion.current().isJava9Compatible) {
        options.release.set(8)
    }
    options.errorprone.disable("StringSplitter")
}

sourceSets {
    test {
        java {
            // Code that is shared with J2Cl tests
            srcDir("src/testFixtures/java")
        }
    }
}

tasks {
    jar {
        from(sourceSets.main.map { it.allJava })
    }

    test {
        val warDir = file("$buildDir/gwt/www-test")
        val workDir = file("$buildDir/gwt/work")
        val cacheDir = file("$buildDir/gwt/cache")
        outputs.dirs(
            mapOf(
                "war" to warDir,
                "work" to workDir,
                "cache" to cacheDir
            )
        )

        classpath += sourceSets.main.get().allJava.sourceDirectories + sourceSets.test.get().allJava.sourceDirectories
        include("**/*Suite.class")
        systemProperty(
            "gwt.args",
            """-ea -draftCompile -batch module -war "$warDir" -workDir "$workDir" -runStyle HtmlUnit:Chrome"""
        )
        systemProperty("gwt.persistentunitcachedir", cacheDir)
    }

    javadoc {
        (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
        if (JavaVersion.current().isJava9Compatible) {
            (options as CoreJavadocOptions).addBooleanOption("html5", true)
        }
        // Workaround for https://github.com/gradle/gradle/issues/5630
        (options as CoreJavadocOptions).addStringOption("sourcepath", "")
    }
}

spotless {
    java {
        target(sourceSets.map { it.allJava }, fileTree("src/j2cl-test/java") { include("**/*.java") })
        googleJavaFormat(libs.versions.googleJavaFormat.get())
        licenseHeaderFile("LICENSE.header")
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
    }
}

//
// J2Cl tests
//
// Because there's only Maven tooling for J2Cl (and specifically tests), we publish
// the JAR to the local Maven repository under a fixed (non-snapshot) version, and
// then fork a Maven build.
//
val j2clTestPublication = publishing.publications.create<MavenPublication>("j2clTest") {
    from(components["java"])
    version = "LOCAL"
}
tasks {
    val j2clTest by registering(Exec::class) {
        shouldRunAfter(test)
        dependsOn("publishJ2clTestPublicationToMavenLocal")
        inputs.files(sourceSets.main.map { it.runtimeClasspath }).withNormalizer(ClasspathNormalizer::class)
        // For the servlets
        inputs.files(compileTestJava).withNormalizer(ClasspathNormalizer::class)
        inputs.file("pom-j2cl-test.xml")
        inputs.dir("src/testFixtures")
        inputs.dir("src/j2cl-test")
        outputs.dir("target")

        val webdriver = findProperty("j2clTest.webdriver") ?: "htmlunit"
        inputs.property("webdriver", webdriver)

        commandLine("mvn", "-V", "-B", "-ntp", "-U", "-e", "-f", "pom-j2cl-test.xml", "verify", "-Dwebdriver=$webdriver")
    }

    check {
        dependsOn(j2clTest)
    }

    withType<PublishToMavenRepository>().configureEach {
        onlyIf { publication != j2clTestPublication }
    }
}
