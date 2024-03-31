
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath 'com.google.gms:google-services:4.3.13'

    }
}
allprojects {
    repositories {
        google()
        maven { url 'https://jitpack.io' }
        mavenCentral()
        jcenter()
    }
    ext {
        _compileSdkVersion = 31
        _minSdkVersion = 21
        _targetSdkVersion = 31
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}