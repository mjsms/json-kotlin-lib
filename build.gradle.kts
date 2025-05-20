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
    // OkHttp para chamadas HTTP em testes ou produção
    testImplementation("com.squareup.okhttp3:okhttp:4.12.0")

    // JUnit para testes
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

/* ----------  ALINHAR AS DUAS COMPILAÇÕES  ---------- */

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

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
