plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.polpick.gemdetector"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.polpick.gemdetector.next"
        minSdk = 23
        targetSdk = 29
        versionCode = 2
        versionName = "NEXT-1.1"
    }

    signingConfigs {
        create("stableNext") {
            val keystorePath = System.getenv("POLPICK_KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("POLPICK_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("POLPICK_KEY_ALIAS")
                keyPassword = System.getenv("POLPICK_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("stableNext")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
