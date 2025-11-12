import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL

plugins {
  id("org.springframework.boot") version "3.4.0"
  id("io.spring.dependency-management") version "1.1.6"
  id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
  kotlin("kapt") version "1.9.25"
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.Embeddable")
  annotation("jakarta.persistence.MappedSuperclass")
}

repositories {
  mavenCentral()
}

extra["springCloudVersion"] = "2024.0.0"
dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

val querydslVersion = dependencyManagement.importedProperties["querydsl.version"]

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("com.github.ben-manes.caffeine:caffeine")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.h2database:h2")
  implementation("com.querydsl:querydsl-jpa:$querydslVersion:jakarta")
  implementation("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
  kapt("com.querydsl:querydsl-apt:$querydslVersion:jakarta")
  kapt("org.springframework.boot:spring-boot-configuration-processor")
  implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(module = "mockito-core")
  }
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
  testImplementation("io.kotest:kotest-framework-datatest:5.9.1")
  testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
  testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter-kotlin:1.1.15")
  testImplementation("com.navercorp.fixturemonkey:fixture-monkey-datafaker:1.1.15")
  testImplementation("com.navercorp.fixturemonkey:fixture-monkey-jackson:1.1.15")
}

springBoot {
  buildInfo {
    properties {
      System.getenv("BUILD_NUMBER")?.let {
        additional.put("build_number", it)
      }
      System.getenv("COMMIT_HASH")?.let {
        additional.put("commit_hash", it)
      }
    }
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
  ktlint {
    verbose.set(true)
    version.set("0.47.1")
  }
  withType<Test> {
    testLogging {
      showStandardStreams = true
      exceptionFormat = FULL
    }
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
