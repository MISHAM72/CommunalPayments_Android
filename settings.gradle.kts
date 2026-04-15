
@file:Suppress("UnstableApiUsage")
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
@Suppress("HardcodedStringLiteral")
rootProject.name = "CommunalPayments_Android"
@Suppress("HardcodedStringLiteral")
include(":app")