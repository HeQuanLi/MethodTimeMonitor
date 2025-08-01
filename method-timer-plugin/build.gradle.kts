import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    kotlin("jvm") version "2.0.21"
    `java-gradle-plugin`
    `maven-publish`
    // signing
}

group = "io.github.hequanli"
version = "1.0.0"

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
            id = "com.methodtimer.plugin"
            implementationClass = "com.hql.methodtimer.plugin.MethodTimerPlugin"
            displayName = "Method Timer Plugin"
            description = "ASM bytecode instrumentation plugin for Android method execution time statistics"
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}


val versionName = "1.0.0"

var signingKeyId = ""//签名的密钥后8位
var signingPassword = ""//签名设置的密码
var secretKeyRingFile = ""//生成的secring.gpg文件目录


val localProperties: File = project.rootProject.file("../local.properties")

if (localProperties.exists()) {
    println("Found secret props file, loading props")
    val properties = Properties()

    InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
        properties.load(reader)
    }
    signingKeyId = properties.getProperty("signing.keyId")
    signingPassword = properties.getProperty("signing.password")
    secretKeyRingFile = properties.getProperty("signing.secretKeyRingFile")

} else {
    println("No props file, loading env vars")
}

afterEvaluate {

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("release"))
                groupId = "io.github.hequanli"
                artifactId = "method-timer-plugin"
                version = versionName

                pom {
                    name.value("method-timer-plugin")
                    description.value("Bytecode instrumentation analysis of main thread method time consumption")
                    url.value("https://github.com/HeQuanLi/MethodTimeMonitor")

                    licenses {
                        license {
                            //协议类型
                            name.value("The MIT License")
                            url.value("https://github.com/HeQuanLi/MethodTimeMonitor/blob/main/LICENSE")
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
        }

        repositories {
            maven {
                setUrl("$rootDir/RepoDir")
            }
        }
    }

}

if (signingKeyId.isNotEmpty() && signingPassword.isNotEmpty() && secretKeyRingFile.isNotEmpty()) {
    apply(plugin = "signing")
    
    gradle.taskGraph.whenReady {
        // if (allTasks.any { it is Sign }) {
            allprojects {
                extra["signing.keyId"] = signingKeyId
                extra["signing.secretKeyRingFile"] = secretKeyRingFile
                extra["signing.password"] = signingPassword
            }
        // }
    }
    
    configure<SigningExtension> {
        sign(publishing.publications)
    }
}