import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serializarion)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.androidx.room)

}
// Save database's schema file version in app/schemas directory
room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.github.umer0586.droidpad"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.umer0586.droidpad"
        minSdk = 23
        targetSdk = 36
        versionCode = 42
        versionName = "3.8.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
     }

    hilt {
        enableAggregatingTask = false
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false

    }

    packaging {
        resources {
            excludes += "bundle.properties"
        }
    }

    sourceSets {
        // Adds exported schema location as test app assets.
        getByName("debug").assets.srcDirs(files("$projectDir/schemas"))
        getByName("release").assets.srcDirs(files("$projectDir/schemas"))
    }

}


dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.org.eclipse.paho.mqttv5.client)
    implementation(libs.org.eclipse.paho.mqttv3.client)

    // Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.core.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.colorpicker.compose)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.network)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.io)
    implementation(libs.java.websocket)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.numberpicker)
    implementation(libs.zxing.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.accompanist.permissions)
    implementation(libs.speedometer.android)


    ksp(libs.hilt.compiler)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.ui.test.junit4)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.turbine)


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)


    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.leakcanary.android)
}