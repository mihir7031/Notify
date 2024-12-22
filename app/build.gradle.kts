//import org.jetbrains.kotlin.gradle.internal.kapt.incremental.UnknownSnapshot.classpath
import org.jetbrains.kotlin.storage.CacheResetOnProcessCanceled.enabled

plugins {
//    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.android.application")
    alias(libs.plugins.google.gms.google.services)
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.notify"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notify"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.auth.v2211)
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.database)
//    implementation(libs.firebase.storage)
//    implementation(libs.firebase.firestore.ktx)
//    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
//
//    implementation("com.google.firebase:firebase-analytics")

    implementation("io.coil-kt:coil:2.2.2")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")



    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //zoomin zoomout dependecies
    implementation("com.github.chrisbanes:PhotoView:2.3.0")


//    implementation (libs.firebase.auth.v2211)
//    implementation ("com.google.firebase:firebase-firestore:24.11.1")
//    implementation (libs.play.services.auth)
    implementation(kotlin("script-runtime"))


}