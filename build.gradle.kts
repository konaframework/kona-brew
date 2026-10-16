import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    group = "io.konaframework"
    version = providers.gradleProperty("brew.version").get()

    repositories {
        mavenLocal {
            content { includeGroup("io.konaframework") }
        }
        mavenCentral()
    }
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            testLogging {
                events("failed")
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                showStackTraces = true
            }
        }
    }

    if (name == "kona-brew") {
        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            configureKonaBrewPublishing()
        }
    }
}

fun Project.configureKonaBrewPublishing() {
    pluginManager.apply("com.vanniktech.maven.publish")
    extensions.configure<MavenPublishBaseExtension> {
        coordinates(group.toString(), "kona-brew", version.toString())
        configure(
            KotlinJvm(
                javadocJar = JavadocJar.Empty(),
                sourcesJar = true,
            ),
        )
        pom {
            name.set("kona-brew")
            description.set("Brew — reactive components for Kona (Livewire-class)")
            url.set("https://github.com/konaframework/kona-brew")
            inceptionYear.set("2025")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("konaframework")
                    name.set("Kona Framework contributors")
                    url.set("https://github.com/konaframework")
                }
            }
            scm {
                url.set("https://github.com/konaframework/kona-brew")
                connection.set("scm:git:git://github.com/konaframework/kona-brew.git")
                developerConnection.set("scm:git:ssh://git@github.com/konaframework/kona-brew.git")
            }
        }
        publishToMavenCentral(automaticRelease = false)
        val hasInMemoryKey =
            findProperty("signingInMemoryKey") != null ||
                System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey") != null
        val hasKeyId = findProperty("signing.keyId") != null
        if (hasInMemoryKey || hasKeyId) {
            signAllPublications()
        }
    }
}
