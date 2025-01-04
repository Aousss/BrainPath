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
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    //add firebasedatabase
    implementation("com.google.firebase:firebase-database")

// Firestore SDK for Kotlin
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation ("com.google.firebase:firebase-storage:21.0.1") // Add this line for Firebase Storage
    implementation ("com.google.firebase:firebase-auth:23.1.0")   // Firebase Auth dependency (if needed)
    implementation ("com.google.firebase:firebase-firestore:25.0.0") // Firestore dependency (if needed)

    // Add other Firebase dependencies as needed

    // Add Glide Library - use for the efficiently loading and caching images, especially from the network or other sources such as Firebase Storage
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
}

