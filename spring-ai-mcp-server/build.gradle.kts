plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.6"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"
description = "spring-ai-mcp-server"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

val springAiVersion = "1.0.2"

dependencies {
  implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
  imports {
    mavenBom("org.springframework.ai:spring-ai-bom:${springAiVersion}")
  }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

tasks {
  test {
    useJUnitPlatform()
  }
  processResources {
    filesMatching("**/application.yml") {
      expand(project.properties)
    }
  }
  jar {
    enabled = false
  }
}
