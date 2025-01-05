plugins {
    alias(libs.plugins.android.application)
    // Apply the Google Services plugin for Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.module2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.module2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        multiDexEnabled = true

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
    buildFeatures{
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")  // Or latest version

    implementation("com.makeramen:roundedimageview:2.3.0")

    implementation(libs.firebase.bom)
    implementation(libs.multidex)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
//    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.roundedimageview)
    implementation(libs.intuit.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.multidex)
    implementation(libs.firebase.messaging)
    implementation(libs.roundedimageview)
    implementation(libs.intuit.sdp.android)
    implementation(libs.ssp.android)

    // Firebase dependencies
    implementation(platform(libs.firebase.bom)) // Firebase BOM for version management
    implementation(libs.firebase.analytics.ktx) // Analytics
    implementation(libs.firebase.database.ktx) // Realtime Database
    implementation(libs.firebase.auth.ktx) // Authentication
    implementation(libs.firebase.firestore.ktx) // Firestore
    implementation(libs.firebase.storage.ktx) // Storage

    // Room dependencies
    implementation("androidx.room:room-runtime:2.5.2") // Core Room library
    annotationProcessor("androidx.room:room-compiler:2.5.2") // Annotation processor for Java


    implementation(libs.firebase.bom)
    implementation(libs.multidex)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging)
    implementation(libs.roundedimageview)
    implementation(libs.intuit.sdp.android)
    implementation(libs.ssp.android)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    implementation("com.intuit.sdp:intuit-sdp-android:1.0.6")
//    implementation("com.ssp:ssp-android:1.0.6")


}

// Apply the Google Services plugin
apply(plugin = "com.google.gms.google-services")
