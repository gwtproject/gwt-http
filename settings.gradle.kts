pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "gwt-http"

buildscript {
    dependencyLocking {
        lockAllConfigurations()
        lockMode.set(LockMode.STRICT)
    }
}
