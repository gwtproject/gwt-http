plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    alias(libs.plugins.spotless)
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
        ktlint(libs.versions.ktlint.get())
    }
}
