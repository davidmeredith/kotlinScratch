import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //kotlin("jvm") version "1.9.23"
     kotlin("jvm") version "2.2.0"
    //kotlin("jvm") version "1.9.23"
    //kotlin("jvm") version "1.6.21"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    //implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}
tasks.withType<KotlinCompile> {
//    kotlinOptions.jvmTarget = "1.8"
//    kotlinOptions.languageVersion = "1.8" // needed for Generics on inline classes (only on jvm for now)
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-parameters")
    }
}

application {
    mainClass.set("MainKt")
}