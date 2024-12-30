plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Add the Google services plugin
}

android {
    namespace = "com.example.brainpath"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.brainpath"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "30.0.3"
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Add Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Add Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    // Auth
    implementation("com.google.firebase:firebase-auth") // Add Firebase Authentication

    //Auth via google
    implementation("com.google.android.gms:play-services-auth:20.6.0")

    //add firebasedatabase
    implementation("com.google.firebase:firebase-database")


    // Add other Firebase dependencies as needed
}
