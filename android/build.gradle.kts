
val version = rootProject.extra["version"] as String

val versionCode = version.replace(".", "").toInt()

println("---------------------------------------- $version ($versionCode) android")

plugins {
    id("org.jetbrains.compose")
    id("com.android.application")
    kotlin("android")
}

repositories {
    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.8.0-alpha02")
}

android {
    compileSdkVersion(33)
    defaultConfig {
        applicationId = "com.hg42.crossplatform.android"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = versionCode
        versionName = version
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    namespace = "com.hg42.android"
}
