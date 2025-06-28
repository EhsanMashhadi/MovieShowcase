import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
    kotlin("plugin.serialization") version "2.0.21"
    alias(libs.plugins.google.services)
}

android {
    namespace = "software.ehsan.movieshowcase"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "software.ehsan.movieshowcase"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        val passedVersionCode = project.findProperty("versionCode")?.toString()
        val versionNameSuffix = project.findProperty("versionNameSuffix")?.toString() ?: ""

        if (!passedVersionCode.isNullOrEmpty()) {
            versionCode = passedVersionCode.toInt()
        }
        if (versionNameSuffix.isNotEmpty()) {
            versionName = "${defaultConfig.versionName}-nightly.${versionNameSuffix}"
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val apiAuthToken = getApiAuthToken()
        buildConfigField("String", "AUTHORIZATION_TOKEN", "\"${apiAuthToken}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
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
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.retrofit)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.converter.moshi)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.paging.compose)


    debugImplementation(libs.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.paging.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.ui.test.junit4)

}

fun getApiAuthToken(): String {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { inputStream ->
            localProperties.load(inputStream)
        }
        val tokenFromLocal = localProperties.getProperty("authorizationToken")
        if (!tokenFromLocal.isNullOrEmpty()) {
            return tokenFromLocal
        }
    }

    val tokenFromEnv = System.getenv("AUTHORIZATION_TOKEN")
    if (!tokenFromEnv.isNullOrEmpty()) {
        return tokenFromEnv
    }

    throw IllegalStateException("Authorization token not found. Please set 'authorizationToken' in local.properties or as an environment variable 'AUTHORIZATION_TOKEN'.")
}