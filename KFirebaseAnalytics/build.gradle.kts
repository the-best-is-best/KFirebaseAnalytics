@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.maven.publish)
}




apply(plugin = "maven-publish")
apply(plugin = "signing")


tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}


extra["packageNameSpace"] = "io.github.kfirebase_analytics"
extra["groupId"] = "io.github.the-best-is-best"
extra["artifactId"] = "kfirebase-analytics"
extra["version"] = "1.2.1"
extra["packageName"] = "KFirebaseAnalytics"
extra["packageUrl"] = "https://github.com/the-best-is-best/KFirebaseAnalytics"
extra["packageDescription"] = "KFirebaseAnalytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Analytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform"
extra["system"] = "GITHUB"
extra["issueUrl"] = "https://github.com/the-best-is-best/KFirebaseAnalytics/issues"
extra["connectionGit"] = "https://github.com/the-best-is-best/KFirebaseAnalytics.git"

extra["developerName"] = "Michelle Raouf"
extra["developerNameId"] = "MichelleRaouf"
extra["developerEmail"] = "eng.michelle.raouf@gmail.com"


mavenPublishing {
    coordinates(
        extra["groupId"].toString(),
        extra["artifactId"].toString(),
        extra["version"].toString()
    )

    publishToMavenCentral(SonatypeHost.S01, true)
    signAllPublications()

    pom {
        name.set(extra["packageName"].toString())
        description.set(extra["packageDescription"].toString())
        url.set(extra["packageUrl"].toString())
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        issueManagement {
            system.set(extra["system"].toString())
            url.set(extra["issueUrl"].toString())
        }
        scm {
            connection.set(extra["connectionGit"].toString())
            url.set(extra["packageUrl"].toString())
        }
        developers {
            developer {
                id.set(extra["developerNameId"].toString())
                name.set(extra["developerName"].toString())
                email.set(extra["developerEmail"].toString())
            }
        }
    }

}


signing {
    useGpgCmd()
    sign(publishing.publications)
}

val packageName = extra["packageName"].toString()

kotlin {
    jvmToolchain(17)
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

//    jvm()
//
//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),

    ).forEach {
        it.binaries.framework {
            baseName = packageName
        }
        it.compilations.getByName("main") {
            val defFileName = when (target.name) {
                "iosX64" -> "iosX64.def"
                "iosArm64" -> "iosArm64.def"
                "iosSimulatorArm64" -> "iosSimulatorArm64.def"
                "macosX64" -> "macosX64.def"
                "macosArm64" -> "macosArm64.def"
                "tvosX64" -> "tvosX64.def"
                "tvosArm64" -> "tvosArm64.def"
                "tvosSimulatorArm64" -> "tvosSimulatorArm64.def"

                else -> throw IllegalStateException("Unsupported target: ${target.name}")
            }

            val defFile = project.file("src/interop/$defFileName")
            if (defFile.exists()) {
                cinterops.create("FirebaseAnalytics") {
                    defFile(defFile)
                    packageName = "io.github.native.kfirebaseanalytics"
                }
            } else {
                logger.warn("Def file not found for target ${target.name}: ${defFile.absolutePath}")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material3)
//            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)

            api(libs.kfirebase.core)

        }

        commonTest.dependencies {
            // implementation(kotlin("test"))
//            @OptIn(ExperimentalComposeLibrary::class)
//            implementation(compose.uiTest)
        }

        androidMain.dependencies {
//            implementation(compose.uiTooling)
//            implementation(libs.androidx.activityCompose)

            implementation(libs.firebase.analytics)
//            implementation(libs.androidx.startup.runtime)
            implementation(libs.firebase.analytics.ktx)
        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//
//        jsMain.dependencies {
//            implementation(compose.html.core)
//        }

        iosMain.dependencies {
        }

    }
}


android {
    namespace = extra["packageNameSpace"].toString()
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
//        buildFeatures {
//            //enables a Compose tooling support in the AndroidStudio
//            compose = true
//        }
    }
}

abstract class GenerateDefFilesTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val interopDir: DirectoryProperty

    @TaskAction
    fun generate() {
        // Ensure the directory exists
        interopDir.get().asFile.mkdirs()

        // Constants
        val firebaseAnalyticsHeaders = "FirebaseAnalytics.framework/Headers/FirebaseAnalytics.h"

        // Map targets to their respective paths
        val targetToPath = mapOf(
            "iosX64" to "ios-arm64_x86_64-simulator",
            "iosArm64" to "ios-arm64",
            "iosSimulatorArm64" to "ios-arm64_x86_64-simulator",
            "macosX64" to "macos-arm64_x86_64",
            "macosArm64" to "macos-arm64_x86_64",
            "tvosArm64" to "tvos-arm64",
            "tvosX64" to "tvos-arm64_x86_64-simulator",
            "tvosSimulatorArm64" to "tvos-arm64_x86_64-simulator",
        )

        // Helper function to generate header paths
        fun headerPath(target: String): String {
            return interopDir.dir("libs/${targetToPath[target]}/$firebaseAnalyticsHeaders").get().asFile.absolutePath
        }

        // Generate headerPaths dynamically
        val headerPaths = targetToPath.mapValues { (target, _) ->
            headerPath(target)
        }

        // List of targets derived from targetToPath keys
        val iosTargets = targetToPath.keys.toList()

        // Loop through the targets and create the .def files
        iosTargets.forEach { target ->
            val headerPath = headerPaths[target] ?: return@forEach
            val defFile = File(interopDir.get().asFile, "$target.def")

            // Generate the content for the .def file
            val content = """
                language = Objective-C
                package = ${packageName.get()}
                headers = $headerPath
            """.trimIndent()

            // Write content to the .def file
            defFile.writeText(content)
            println("Generated: ${defFile.absolutePath} with headers = $headerPath")
        }
    }
}
// Register the task within the Gradle build
tasks.register<GenerateDefFilesTask>("generateDefFiles") {
    packageName.set("io.github.native.kfirebaseanalytics")
    interopDir.set(project.layout.projectDirectory.dir("src/interop"))
}


