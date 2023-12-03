pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

//includeBuild("<androidx-compose-material3-pullrefresh-path>/library") {
//    dependencySubstitution {
//        substitute(module("me.omico.compose:compose-material3-pullrefresh")).using(project(":"))
//    }
//}

rootProject.name = "Trem"
include(":app")
 