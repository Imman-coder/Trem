plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.immanlv.trem"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.immanlv.trem"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.3.23 beta"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3:1.2.0-alpha12")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.compose:compose-bom:2023.10.01")
    implementation ("androidx.core:core-ktx:{latestVersion}")



    // Dagger Hilt dependencies
    val hilt = "2.48.1"
    implementation ("com.google.dagger:hilt-android:$hilt")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt")
    kapt ("androidx.hilt:hilt-compiler:1.1.0")

    // Datastore dependencies
    implementation ("androidx.datastore:datastore:1.0.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.5")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("androidx.compose.material3:material3-window-size-class")


    // Retrofit dependencies
    val retrofit = "2.9.0"
    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
    implementation ("com.squareup.retrofit2:converter-gson:$retrofit")

    implementation ("com.squareup.okhttp3:logging-interceptor:3.4.1")
    implementation ("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation ("com.squareup.okhttp3:okhttp-urlconnection:3.6.0")

    // GridPad
    implementation ("com.touchlane:gridpad:1.1.0")

    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")

    //
    implementation ("com.itextpdf:itextpdf:5.5.13.3")

    //
    implementation ("androidx.compose.foundation:foundation:1.5.4")

    // Splashscreen API
    implementation ("androidx.core:core-splashscreen:1.0.1")

    // Datastore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    // Compose dependencies
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")
    implementation ("androidx.hilt:hilt-navigation-compose:1.1.0")

    implementation("org.jsoup:jsoup:1.14.3")


    // Splashscreen dependency
    implementation("androidx.core:core-splashscreen:1.0.1")


    implementation("io.github.vanpra.compose-material-dialogs:datetime:0.8.1-rc")
    coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:1.1.6")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}