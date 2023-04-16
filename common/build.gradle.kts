
val version = rootProject.extra["version"] as String

println("---------------------------------------- $version common")

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    android()
    jvm("desktop") {
        jvmToolchain(11)
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                //@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                //implementation(compose.components.resources)

                ////implementation(kotlin("script-util"))
                ////implementation(kotlin("script-runtime"))
                ////implementation(kotlin("compiler-embeddable"))
                ////implementation(kotlin("scripting-common"))
                ////implementation(kotlin("scripting-jvm"))
                ////implementation(kotlin("scripting-jvm-host"))
                ////implementation(kotlin("scripting-compiler-embeddable"))
                //////implementation(kotlin("scripting-compiler-dependencies"))
                runtimeOnly(kotlin("scripting-jsr223"))

                implementation("org.jruby:jruby-complete:9.3.8.0")

                ////implementation("org.python:jython-standalone:2.7.3")
                ////implementation("org.python:jython:2.7.3")
                //implementation("org.python:jython-slim:2.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.hg42.common"
}
