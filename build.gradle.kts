plugins {
    kotlin("jvm") version "2.0.0"
}

group = "amaiice.dicebot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // Kord Snapshots Repository (Optional):
}

dependencies {
    implementation("dev.kord:kord-core:0.14.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(16)
}