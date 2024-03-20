plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
//    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
//    id("kotlin-kapt")
}

android {
    namespace = "com.example.partyapp"
    compileSdk = 34

    defaultConfig {
        var MAPS_API_KEY = "AIzaSyBfa40xoO4qbENlMV_rkVpwYdtc-3kFvrY"
        applicationId = "com.example.partyapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        /*
    buildscript {
        dependencies {
            classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        }
    }
    */
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
        kotlinOptions {
            jvmTarget = "1.8"
        }
        buildFeatures {
            compose = true
            viewBinding = true
            dataBinding = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.0"
        }

    }

    dependencies {
        implementation("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
        implementation("androidx.core:core-ktx:1.9.0")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.10.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
        implementation("androidx.activity:activity-compose:1.8.1")
        implementation(platform("androidx.compose:compose-bom:2023.03.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.ui:ui-graphics")
        implementation("androidx.compose.ui:ui-tooling-preview")
        implementation("com.google.android.gms:play-services-maps:18.2.0")
        implementation("androidx.compose.material3:material3")
        implementation("com.google.firebase:firebase-database:20.3.1")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.5")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
        androidTestImplementation("androidx.compose.ui:ui-test-junit4")
        debugImplementation("androidx.compose.ui:ui-tooling")
        implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
        debugImplementation("androidx.compose.ui:ui-test-manifest")
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.android.gms:play-services-location:17.0.0")
        implementation("org.threeten:threetenbp:1.4.0")
        implementation("com.google.code.gson:gson:2.10.1")
        testImplementation("junit:junit:4.12")
        androidTestImplementation("androidx.test.ext:junit:1.1.1")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
//    kapt("com.github.bumptech.glide:compiler:4.14.2")
        implementation("androidx.cardview:cardview:1.0.0")

    }
}

