plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
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
    jcenter()
}
kotlinDslPluginOptions {
    experimentalWarning.set(false)
}
ktlint {
    version.set("0.40.0")
    enableExperimentalRules.set(true)
    kotlinScriptAdditionalPaths {
        include(fileTree("src/main/kotlin"))
    }
    filter {
        exclude {
            it.file in fileTree(buildDir)
        }
    }
}
