plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.duofold"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.duofold"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
