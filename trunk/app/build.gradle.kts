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

    packaging {
        resources {
            excludes += setOf(
                "META-INF/*.SF",
                "META-INF/*.RSA",
                "META-INF/*.DSA"
            )
        }
    }
}

dependencies {
    implementation(files("libs/aiCharts.2.0.0.182.84147.jar"))
    implementation(files("libs/opencsv-1.8.jar"))

    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:core:1.6.1")
}
