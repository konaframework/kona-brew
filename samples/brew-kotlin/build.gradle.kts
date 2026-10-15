plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("bootstrap.App")
}

dependencies {
    implementation(project(":kona-brew"))
    implementation(libs.kona.http)
    implementation(libs.kona.session)
    implementation(libs.kona.crema)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
