plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.8.20"

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
}

group = "me.dzikimlecz.libgtfskt"
version = "0.1"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.15.2")

    runtimeOnly("org.slf4j:slf4j-api:2.0.5")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.5")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.named<Test>("test") {
      useJUnitPlatform()
}
