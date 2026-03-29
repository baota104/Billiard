plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")  // Bắt buộc phải có để Hilt sinh code
    id ("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.billiard"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.billiard"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

}

dependencies {
    // 1. CORE ANDROIDX & GIAO DIỆN (XML)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation ("com.google.dagger:hilt-android:2.51")
    kapt ("com.google.dagger:hilt-compiler:2.51")

    // 2. LIFECYCLE & VIEWMODEL (Dùng cho Clean Architecture)
    val lifecycle_version = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    // Hỗ trợ khởi tạo ViewModel nhanh trong Activity/Fragment bằng từ khóa 'by viewModels()'
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // --- Navigation ---
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // 3. COROUTINES (Xử lý bất đồng bộ - Thay thế Thread)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // 4. RETROFIT & GSON (Gọi API lên Spring Boot)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Chuyển JSON thành Object

    // 5. OKHTTP LOGGING INTERCEPTOR (Cực kỳ quan trọng để debug API)
    // Giúp bạn xem được Request gửi đi và Response trả về trên màn hình Logcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

     implementation("com.github.bumptech.glide:glide:4.16.0")
}