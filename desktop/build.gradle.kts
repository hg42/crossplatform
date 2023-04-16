import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val version = rootProject.extra["version"] as String

println("---------------------------------------- $version desktop")

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = rootProject.name
            packageVersion = version

            windows {
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "a861cedc-aae2-4dd3-ada6-9d17314bb6dc"
            }

            macOS {
                bundleID = rootProject.name
            }
        }
    }
}
