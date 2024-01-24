buildscript {
    val gradlePluginVersion = "8.2.0"
    val kotlinVersion = "1.6.0"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$gradlePluginVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("androidx.annotation:annotation:1.6.0")
    }
}

allprojects {
    repositories {
    }
}
