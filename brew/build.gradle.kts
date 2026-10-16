plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kover)
    `java-library`
}

dependencies {
    api(libs.kona.core)
    api(libs.kona.http)
    api(libs.kona.crema)
    api(libs.kona.session)
    api(libs.kona.support)
    api(libs.kona.auth)
    api(libs.kona.cadence)
    api(libs.kona.filesystem)
    api(libs.jackson.databind)
    implementation(libs.jackson.module.kotlin)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${libs.versions.jackson.get()}")
    compileOnly(libs.kona.grind)

    testImplementation(libs.kona.grind)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        javaParameters.set(true)
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

kover {
    reports {
        total {
            verify {
                rule {
                    bound {
                        minValue = 100
                        coverageUnits.set(kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE)
                    }
                    bound {
                        minValue = 100
                        coverageUnits.set(kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH)
                    }
                }
            }
        }
    }
}

tasks.register<JavaExec>("bench") {
    description = "Runs the Brew server hot-path baseline."
    group = "verification"
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("io.konaframework.brew.bench.BrewBench")
    providers.gradleProperty("benchIterations").orNull?.let { args(it) }
}
