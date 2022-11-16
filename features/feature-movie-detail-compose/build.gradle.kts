plugins {
    id("hi.android.library")
    id("hi.android.feature")
    id("hi.android.library.compose")
    id("hi.android.library.jacoco")
    id("dagger.hilt.android.plugin")
    id("hi.spotless")
}

dependencies {
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.com.google.accompanist.pager)
    implementation(libs.com.google.accompanist.swiperefresh)
    implementation(libs.com.google.accompanist.flowlayout)
}
android {
    namespace = "com.baka3k.architecture.feature.movie.detail"
}
