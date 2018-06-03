import java.time.Year

plugins {
    `java-library`
    maven
    signing
    id("net.ltgt.errorprone") version "0.0.14"
    id("com.github.sherter.google-java-format") version "0.6"
    id("com.github.hierynomus.license") version "0.14.0"
}

group = "org.gwtproject.http"
version = "HEAD-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.3.1")

    implementation("com.google.elemental2:elemental2-dom:1.0.0-RC1")
    implementation("com.google.elemental2:elemental2-core:1.0.0-RC1")
    implementation("com.google.jsinterop:base:1.0.0-RC1")

    testImplementation("junit:junit:4.12")
    testImplementation("com.google.gwt:gwt-user:2.8.2")
    testImplementation("com.google.gwt:gwt-dev:2.8.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Werror", "-Xlint:all", "-Xep:StringSplitter:OFF"))
    if (JavaVersion.current().isJava9Compatible) {
        options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
}

val jar by tasks.getting(Jar::class) {
    from(java.sourceSets["main"].allJava)
}

val test by tasks.getting(Test::class) {
    val warDir = file("$buildDir/gwt/www-test")
    val workDir = file("$buildDir/gwt/work")
    val cacheDir = file("$buildDir/gwt/cache")
    outputs.dirs(mapOf(
        "war" to warDir,
        "work" to workDir,
        "cache" to cacheDir
    ))

    classpath += java.sourceSets["main"].allJava.sourceDirectories + java.sourceSets["test"].allJava.sourceDirectories
    include("**/*Suite.class")
    systemProperty("gwt.args", """-ea -draftCompile -batch module -war "$warDir" -workDir "$workDir" -runStyle HtmlUnit:Chrome""")
    systemProperty("gwt.persistentunitcachedir", cacheDir)
}

val javadoc by tasks.getting(Javadoc::class) {
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
}

googleJavaFormat {
    toolVersion = "1.5"
}

license {
    header = rootProject.file("LICENSE.header")
    encoding = "UTF-8"
    skipExistingHeaders = true
    mapping("java", "SLASHSTAR_STYLE")

    extra["year"] = Year.now()
    extra["name"] = "The GWT Project Authors"
}

apply {
    from("gradle/ktlint.gradle.kts")
}
