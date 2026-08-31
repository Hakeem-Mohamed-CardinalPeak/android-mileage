plugins {
    id("com.android.application")
}

android {
    namespace = "com.evancharlton.mileage"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.evancharlton.mileage"
        minSdk = 24
        targetSdk = 36
        versionCode = 3110
        versionName = "3.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

}

dependencies {
    implementation(files("libs/opencsv-1.8.jar"))

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.fragment:fragment:1.8.4")
    implementation("androidx.core:core:1.13.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
}
