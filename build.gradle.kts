import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.compile.JavaCompile

plugins {
    kotlin("jvm") version "2.1.21"
}

group = "io.github.mjsms"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}

/* ----------  ALINHAR AS DUAS COMPILAÇÕES  ---------- */

// Kotlin → byte-code 17
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

// Java → byte-code 17, mesmo usando JDK 23
java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility  = JavaVersion.VERSION_21
}
tasks.withType<JavaCompile> {
    options.release.set(21)
}

/* --------------------------------------------------- */

java { withSourcesJar() }

tasks.jar {
    manifest.attributes["Automatic-Module-Name"] = "json.kotlin.lib"
}
