pluginManagement {
    repositories {
        google()          // Repositorio de plugins de Android
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()          // Repositorio de dependencias normales
        mavenCentral()
    }
}

rootProject.name = "TOTT"
include(":app")
