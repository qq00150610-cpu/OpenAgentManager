pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    @Suppress("UnstableApiUsage")
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
