plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false // Ensure correct plugin version
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2") // Google Services Plugin
        classpath ("com.android.tools.build:gradle:7.4.0")
    }
}

tasks.register("signingReport") {
    doLast {
        println("SHA-1 fingerprint generation is not directly supported in Gradle Kotlin DSL.")
        println("Use the command line or Android Studio's signing report.")
    }
}