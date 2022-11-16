@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("hi.android.library")
    id("hi.android.library.jacoco")
    id("kotlinx-serialization")
    alias(libs.plugins.ksp)
    id("hi.spotless")
}
dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
}
android {
    namespace = "com.baka3k.core.model"
}
