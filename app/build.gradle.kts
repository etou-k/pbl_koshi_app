plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pbl_koshi_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pbl_koshi_app"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // --- 基本ライブラリ (変更なし) ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // --- その他のライブラリ (変更なし) ---
    // Google Play services for location
    implementation("com.google.android.gms:play-services-location:21.2.0")
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // --- ★★★ ここからが修正箇所 ★★★ ---

    // 既存のナビゲーションライブラリの定義をすべてコメントアウト（または削除）
    // implementation(libs.navigation.fragment)
    // implementation(libs.navigation.ui)
    // implementation(libs.androidx.navigation.fragment)
    // implementation(libs.androidx.navigation.ui)
    // implementation(libs.androidx.navigation.navigation.fragment)

    // 代わりに、バージョンを統一したナビゲーションライブラリを直接指定
    val nav_version = "2.7.7" // 安定バージョンを指定
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    // --- テストライブラリ (変更なし) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

