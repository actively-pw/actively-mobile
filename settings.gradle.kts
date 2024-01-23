import java.util.Properties

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

val apiKeysProperties = Properties()
file("apikey.properties").also { apiKeysProperties.load(it.inputStream()) }

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://api.mapbox.com/downloads/v2/releases/maven") {
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = apiKeysProperties.getProperty("MAPBOX_PRIVATE_TOKEN")
            }
        }
    }
}

rootProject.name = "My FitBook"
include(":app")
