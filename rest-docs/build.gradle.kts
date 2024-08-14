import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.ask"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val asciidoctorExt: Configuration by configurations.creating

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

val snippetsDir = file("build/generated-snippets")

tasks.test {
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading")
    outputs.dir(snippetsDir)
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.asciidoctor {
    dependsOn(tasks.test)
    inputs.dir(snippetsDir)
    configurations(asciidoctorExt.name)
    baseDirFollowsSourceFile()
}

tasks.bootJar {
    dependsOn(tasks.asciidoctor)
    from ("build/docs/asciidoc") {
        into("BOOT-INF/classes/static/docs")
    }
}
