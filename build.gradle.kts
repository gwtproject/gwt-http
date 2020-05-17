import java.time.Year
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    id("local.maven-publish")
    id("net.ltgt.errorprone") version "1.1.1"
    id("com.github.sherter.google-java-format") version "0.8"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("com.github.hierynomus.license") version "0.15.0"
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
version = "HEAD-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.3.4")
    errorproneJavac("com.google.errorprone:javac:9+181-r4173-1")

    implementation("com.google.elemental2:elemental2-dom:1.0.0")
    implementation("com.google.elemental2:elemental2-core:1.0.0")
    implementation("com.google.jsinterop:base:1.0.0")

    testImplementation("junit:junit:4.13")
    testImplementation("com.google.gwt:gwt-user:2.9.0")
    testImplementation("com.google.gwt:gwt-dev:2.9.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Werror", "-Xlint:all"))
    if (JavaVersion.current().isJava9Compatible) {
        options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
    options.errorprone.check("StringSplitter", CheckSeverity.OFF)
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
        // Workaround for https://github.com/gradle/gradle/issues/5630
        (options as CoreJavadocOptions).addStringOption("sourcepath", "")
    }
}

googleJavaFormat {
    toolVersion = "1.7"
}
ktlint {
    version.set("0.36.0")
    enableExperimentalRules.set(true)
}

license {
    header = rootProject.file("LICENSE.header")
    encoding = "UTF-8"
    skipExistingHeaders = true
    mapping("java", "SLASHSTAR_STYLE")

    extra["year"] = Year.now()
    extra["name"] = "The GWT Project Authors"
}

open class J2clTranspile : SourceTask() {
    @CompileClasspath lateinit var classpath: FileCollection
    @InputFiles
    @SkipWhenEmpty
    @PathSensitive(PathSensitivity.NAME_ONLY)
    override fun getSource(): FileTree = super.getSource()
    @OutputFile lateinit var destinationFile: File
    @Classpath lateinit var j2clClasspath: FileCollection

    @TaskAction
    fun compile() {
        // TODO: incremental transpilation
        project.javaexec {
            main = "com.google.j2cl.transpiler.J2clCommandLineRunner"
            classpath = j2clClasspath
            args("-cp", this@J2clTranspile.classpath.asPath, "-d", destinationFile.path)
            args(source)
        }
    }
}

project.findProperty("j2cl.transpiler.path")?.also { j2clTranspilerPath ->
    val j2clJrePath = project.property("j2cl.jre.path")
    tasks {
        val j2clTranspile by registering(J2clTranspile::class) {
            destinationFile = project.file("$buildDir/j2clTranspile.zip")
            j2clClasspath = project.files(j2clTranspilerPath)
            source(sourceSets.main.map { it.allJava })
            classpath = project.files(j2clJrePath, configurations.compileClasspath)
        }
        check { dependsOn(j2clTranspile) }
    }
}
