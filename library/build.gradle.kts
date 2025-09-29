import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    kotlin("plugin.serialization") version "1.9.20"
}

val firebaseVersion = "2.1.0"
group = "io.github.kotlin"
version = "1.0.0"

kotlin {
    val xcframeworkName = "KMMAnalytics"
    val xcf = XCFramework(xcframeworkName)

    jvm()

    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = xcframeworkName
            binaryOption("bundleId", "org.aetherius.${xcframeworkName}")
            xcf.add(this)
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("dev.gitlive:firebase-firestore:$firebaseVersion")
                implementation("dev.gitlive:firebase-common:$firebaseVersion")
                implementation("dev.gitlive:firebase-auth:$firebaseVersion")
                implementation("dev.gitlive:firebase-storage:$firebaseVersion")
                implementation("dev.gitlive:firebase-functions:$firebaseVersion")
                implementation("dev.gitlive:firebase-analytics:$firebaseVersion")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }

}

android {
    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "library", version.toString())

    pom {
        name = "My library"
        description = "A library."
        inceptionYear = "2024"
        url = "https://github.com/kotlin/multiplatform-library-template/"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}

// --- XCFramework + Swift Package export tasks ---

val xcframeworkName = "KMMAnalytics"

// Task to generate a Swift Package wrapping the XCFramework
tasks.register("swiftPackage") {
    group = "build"
    description = "Generate Swift Package manifest and bundle for $xcframeworkName"

    doLast {
        val packageDir = layout.buildDirectory.dir("swiftPackage/${xcframeworkName}Package").get().asFile
        packageDir.mkdirs()

        // Copy XCFramework output
        copy {
            from(layout.buildDirectory.dir("XCFrameworks/release"))
            include("${xcframeworkName}.xcframework/**")
            into(packageDir)
        }

        // Write Package.swift
        val packageSwift = packageDir.resolve("Package.swift")
        packageSwift.writeText(
            """
            // swift-tools-version:5.5
            import PackageDescription

            let package = Package(
                name: "${xcframeworkName}Package",
                platforms: [
                    .iOS(.v13)
                ],
                products: [
                    .library(
                        name: "${xcframeworkName}",
                        targets: ["${xcframeworkName}"]
                    ),
                ],
                targets: [
                    .binaryTarget(
                        name: "${xcframeworkName}",
                        path: "${xcframeworkName}.xcframework"
                    )
                ]
            )
            """.trimIndent()
        )
    }
}

val generateEvents by tasks.registering {
    val inputFile = layout.projectDirectory.file("Events.json")
    val outputDir = project.file("src/commonMain/kotlin")

    inputs.file(inputFile)
    outputs.dir(outputDir)

    doLast {
        val json = JsonSlurper().parse(inputFile.asFile)
        val sb = StringBuilder()
        sb.appendLine("import dev.gitlive.firebase.Firebase")
        sb.appendLine("import dev.gitlive.firebase.analytics.analytics")
        sb.appendLine()
        sb.appendLine("// AUTO-GENERATED FILE. DO NOT EDIT.")

        (json as List<Map<*, *>>).forEach { event ->
            val name = event["name"] as String
            val params = event["params"] as List<Map<*, *>>

            // function signature
            val fnParams = params.joinToString(", ") { p ->
                val type = when (p["type"]) {
                    "int" -> "Int"
                    "string" -> "String"
                    else -> "String" // default
                }
                "${p["name"]}: $type"
            }

            sb.appendLine("fun $name($fnParams) {")
            sb.appendLine("    Firebase.analytics.logEvent(")
            sb.appendLine("        name = \"$name\",")
            sb.appendLine("        parameters = mapOf(")

            val mapEntries = params.joinToString(",\n") { p ->
                "            \"${p["name"]}\" to ${p["name"]}"
            }

            sb.appendLine(mapEntries)
            sb.appendLine("        )")
            sb.appendLine("    )")
            sb.appendLine("}")
            sb.appendLine()
        }

        val outputFile = outputDir.resolve("AnalyticsEvents.kt")
        outputFile.parentFile.mkdirs()
        outputFile.writeText(sb.toString())
    }
}

// Add generated sources to the source set
kotlin.sourceSets["commonMain"].kotlin.srcDir(
    tasks.named("generateEvents").map {
        layout.buildDirectory.dir("generated/source/events")
    }
)
