plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "se.jonas.horselhjalp"
    compileSdk = 34

    defaultConfig {
        applicationId = "se.jonas.horselhjalp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // Custom release signing configuration when keystore is provided
        create("release") {
            // These properties are injected via command line during CI build
            val keystoreFile = project.findProperty("android.injected.signing.store.file") as String?
            val keystorePassword = project.findProperty("android.injected.signing.store.password") as String?
            val keyAliasValue = project.findProperty("android.injected.signing.key.alias") as String?
            val keyPasswordValue = project.findProperty("android.injected.signing.key.password") as String?
            
            if (keystoreFile != null && keystorePassword != null && keyAliasValue != null && keyPasswordValue != null) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                keyAlias = keyAliasValue
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // Use custom signing config if available, otherwise fall back to debug signing
            // This ensures the APK is always signed and installable
            val releaseSigningConfig = signingConfigs.getByName("release")
            signingConfig = if (releaseSigningConfig.storeFile != null) {
                releaseSigningConfig
            } else {
                signingConfigs.getByName("debug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    kotlin {
        jvmToolchain(17)   // ← lägg till detta rad för att aktivera toolchain (bästa lösningen!)
    }



    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}