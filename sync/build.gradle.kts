plugins {
    id("hi.android.library")
    id("hi.android.library.jacoco")
    kotlin("kapt")
    id("hi.spotless")
}

dependencies {

    implementation(project(path = ":core:core-common"))
    implementation(project(path = ":core:core-model"))
    implementation(project(path = ":core:core-data"))
    implementation(project(path = ":core:core-datastore"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    kapt(libs.hilt.ext.compiler)

    implementation(libs.androidx.startup)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
}
android {
    namespace = "com.baka3k.sync"
}
