import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import java.time.Year

plugins {
    `java-library`
    id("local.maven-publish")
    id("local.ktlint")
    id("net.ltgt.errorprone") version "0.6"
    id("com.github.sherter.google-java-format") version "0.7.1"
    id("com.github.hierynomus.license") version "0.14.0"
}

group = "org.gwtproject.http"
version = "HEAD-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.3.2")
    errorproneJavac("com.google.errorprone:javac:9+181-r4173-1")

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
    options.compilerArgs.addAll(arrayOf("-Werror", "-Xlint:all"))
    if (JavaVersion.current().isJava9Compatible) {
        options.compilerArgs.addAll(arrayOf("--release", "8"))
    }
    options.errorprone.check("StringSplitter", CheckSeverity.OFF)
}

val jar by tasks.getting(Jar::class) {
    from(sourceSets["main"].allJava)
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

    classpath += sourceSets["main"].allJava.sourceDirectories + sourceSets["test"].allJava.sourceDirectories
    include("**/*Suite.class")
    systemProperty("gwt.args", """-ea -draftCompile -batch module -war "$warDir" -workDir "$workDir" -runStyle HtmlUnit:Chrome""")
    systemProperty("gwt.persistentunitcachedir", cacheDir)
}

val javadoc by tasks.getting(Javadoc::class) {
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
}

googleJavaFormat {
    toolVersion = "1.6"
}

license {
    header = rootProject.file("LICENSE.header")
    encoding = "UTF-8"
    skipExistingHeaders = true
    mapping("java", "SLASHSTAR_STYLE")

    extra["year"] = Year.now()
    extra["name"] = "The GWT Project Authors"
}

open class J2clTranspile : DefaultTask() {
    @CompileClasspath lateinit var classpath: FileCollection
    @InputFiles @SkipWhenEmpty @PathSensitive(PathSensitivity.NAME_ONLY) lateinit var source: FileTree
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
    val j2clTranspile by tasks.creating(J2clTranspile::class) {
        destinationFile = project.file("$buildDir/j2clTranspile.zip")
        j2clClasspath = project.files(j2clTranspilerPath)
        source = sourceSets["main"].allJava
        classpath = project.files(j2clJrePath, configurations.compileClasspath)
    }
    tasks["check"].dependsOn(j2clTranspile)
}
