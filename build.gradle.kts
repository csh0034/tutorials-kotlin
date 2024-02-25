plugins {
    kotlin("jvm") version "1.9.20"
}

group = "org.ask"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}
