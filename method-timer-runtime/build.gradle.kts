import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties

plugins {
    id("com.android.library")
    kotlin("android")
    `maven-publish`
    signing
}

group = "com.hql"
version = "1.0.0"

android {
    namespace = "com.methodtimer"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
    
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
}

val versionName = "1.0.0"

var signingKeyId = ""//签名的密钥后8位
var signingPassword = ""//签名设置的密码
var secretKeyRingFile = ""//生成的secring.gpg文件目录


val localProperties: File = project.rootProject.file("local.properties")

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
                artifactId = "method-timer-runtime"
                version = versionName

                pom {
                    name.value("method-timer-runtime")
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

gradle.taskGraph.whenReady {
    if (allTasks.any { it is Sign }) {
        allprojects {
            extra["signing.keyId"] = signingKeyId
            extra["signing.secretKeyRingFile"] = secretKeyRingFile
            extra["signing.password"] = signingPassword
        }
    }
}

signing {
    sign(publishing.publications)
}