
val refTime = java.util.GregorianCalendar(2023, 0, 1).time!! // reference Date
val startTime = java.util.Date()
val seconds = ((startTime.time - refTime.time) / 1000)
val minutes = seconds / 60
val fiveminutes = seconds / 60 / 5
val tenminutes = seconds / 60 / 10
val hours = seconds / 60 / 60
println("seconds:     $seconds")
println("minutes:     $minutes")
println("fiveminutes: $fiveminutes")
println("tenminutes:  $tenminutes")
println("hours:       $hours")

val group by extra { "com.hg42" }
val version by extra { "1.0.$hours" }

println("---------------------------------------- $version root")

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


