import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    kotlin("jvm") version "2.0.21"
    `java-gradle-plugin`
    alias(libs.plugins.vanniktech.mavenPublish)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleApi())
    implementation("org.ow2.asm:asm:9.8")
    implementation("org.ow2.asm:asm-commons:9.8")
    implementation("com.android.tools.build:gradle:8.11.1")
}

gradlePlugin {
    plugins {
        create("methodTimer") {
            id = "io.github.hequanli.method-timer"
            implementationClass = "com.hql.methodtimer.plugin.MethodTimerPlugin"
            displayName = "Method Timer Plugin"
            description = "ASM bytecode instrumentation plugin for Android method execution time statistics"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

var group = ""
var versionName = ""
// included build 的 rootProject 指向 method-timer-plugin/ 目录
// 因此需要 ../ 向上一级才能访问主项目的 gradle.properties
val gradlePropsFile: File = project.rootProject.file("../gradle.properties")

if (gradlePropsFile.exists()) {
    val properties = Properties()
    InputStreamReader(FileInputStream(gradlePropsFile), Charsets.UTF_8).use { reader ->
        properties.load(reader)
    }
    group = properties.getProperty("group")
    versionName = properties.getProperty("versionName")

    val signingKeyId = properties.getProperty("signing.keyId") ?: ""
    val signingPassword = properties.getProperty("signing.password") ?: ""
    val signingSecretKeyRingFile = properties.getProperty("signing.secretKeyRingFile") ?: ""

    if (signingKeyId.isNotEmpty()) {
        extra["signing.keyId"] = signingKeyId
        extra["signing.password"] = signingPassword
        extra["signing.secretKeyRingFile"] = signingSecretKeyRingFile
    }
} else {
    throw Error("gradle.properties not found")
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}

mavenPublishing {
    coordinates(group, "method-timer-plugin", versionName)

    pom {
        name.value("method-timer-plugin")
        description.value("Bytecode instrumentation analysis of main thread method time consumption")
        inceptionYear = "2026"
        url.value("https://github.com/HeQuanLi/MethodTimeMonitor")

        licenses {
            license {
                name.value("The MIT License")
                url.value("https://github.com/HeQuanLi/MethodTimeMonitor/blob/main/LICENSE")
                distribution.set("https://github.com/HeQuanLi/MethodTimeMonitor/blob/main/LICENSE")
            }
        }

        developers {
            developer {
                id.value("HeQaunLi")
                name.value("HeQaunLi")
                email.value("Hunter94520@gmail.com")
            }
        }

        scm {
            connection.value("scm:git@github.com:HeQuanLi/MethodTimeMonitor")
            developerConnection.value("scm:git@github.com:HeQuanLi/MethodTimeMonitor")
            url.value("https://github.com/HeQuanLi/MethodTimeMonitor")
        }
    }
}