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
        versionCode = 1
        versionName = "NEXT-1.0"
    }
}
