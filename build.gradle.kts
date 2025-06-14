// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.android.library") version "8.2.2" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
}
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}