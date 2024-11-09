val kotlin_version: String by project
val logback_version: String by project
val ktor_version: String by project
val docker_username: String by project
val docker_password: String by project
val docker_image_name: String by project
val commons_text_version: String by project

plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
//    id("com.google.cloud.tools.jib") version "3.4.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

group = "io.jcervelin"
version = "0.0.1"

application {
    mainClass.set("io.jcervelin.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set(docker_image_name)
        imageTag.set("0.0.1")
    }
}

//jib {
//    from {
//        image = "eclipse-temurin:17.0.12_7-jdk-focal"
//    }
//    to {
//        auth {
//            username = docker_username
//            password = docker_password
//        }
//        image = docker_image_name
//    }
//    container {
//        mainClass = "io.jcervelin.ApplicationKt"
//    }
//}

dependencies {
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-client-core")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-swagger")
    implementation("io.ktor:ktor-client-logging")
    implementation("io.ktor:ktor-server-websockets")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.apache.commons:commons-text:$commons_text_version")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.0")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-mock")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
