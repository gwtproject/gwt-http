plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.diffplug.spotless") version "6.3.0"
}
buildscript {
    dependencyLocking {
        lockAllConfigurations()
    }
}
dependencyLocking {
    lockAllConfigurations()
}
repositories {
    mavenCentral()
}
kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("0.44.0")
    }
}
