plugins {
    id("hi.android.library")
    id("hi.android.feature")
    id("hi.android.library.jacoco")
    id("dagger.hilt.android.plugin")
    id("hi.spotless")
    id("hi.android.library.compose")
}
android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.baka3k.test.feature.moviedetail"
}
dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.recyclerview)
    // navigation
    implementation(libs.androidx.navigation.dynamic.feature.fragment)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
}
