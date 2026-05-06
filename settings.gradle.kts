pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OpenAgentManager"

include(":app")
include(":core:network")
include(":core:model")
include(":core:ui")
include(":feature:openclaw")
include(":feature:openhermes")
include(":feature:agent-editor")
