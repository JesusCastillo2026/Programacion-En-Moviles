plugins { id("com.android.application"); id("org.jetbrains.kotlin.plugin.compose") }
android {
    namespace = "com.castillo.fit"
    compileSdk = 37
    buildToolsVersion = "37.0.0"
    defaultConfig {
        applicationId = "com.castillo.fit.ia"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    buildFeatures { compose = true }
}
dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.02.01"))
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.navigation:navigation-compose:2.9.7")
    testImplementation("junit:junit:4.13.2")
}
