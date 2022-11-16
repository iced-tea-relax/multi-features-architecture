plugins {
    id("hi.android.library")
    id("hi.android.library.jacoco")
    kotlin("kapt")
    id("hi.spotless")
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
}
android {
    namespace = "com.baka3k.core.common"
}
