plugins {
    // usa el alias del catálogo de versiones para aplicar el plugin android
    alias(libs.plugins.android.application)
    // no aplicar de nuevo `com.android.application` aquí (causa el error de plugin duplicado)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tarea33firebasecrud_gyvenorellana"
    // actualizar compileSdk a 37 porque algunas dependencias requieren API 37+
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.tarea33firebasecrud_gyvenorellana"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    //Dependencia de firebase
    implementation(platform("com.google.firebase:firebase-bom:34.17.0"))
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-auth")
}
