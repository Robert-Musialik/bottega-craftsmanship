import org.jetbrains.kotlin.cli.jvm.main
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCallArgument.DefaultArgument.arguments
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("groovy")
    id("org.liquibase.gradle") version "2.0.4"
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.jpa") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
}

group = "com.vattenfall.emobility"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

project.ext {
    set("spockVersion", "2.0-groovy-3.0")
    set("testContainersVersion", "1.15.3")
}

repositories {
    mavenCentral()
}

dependencies {
    //Spring et al
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux") //for webclient
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    //Kafka
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.testcontainers:kafka:${property("testContainersVersion")}") //to run in integration tests or locally without dependencies

    //kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.microutils:kotlin-logging:2.0.3")

    //DB
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    implementation("com.vladmihalcea:hibernate-types-52:2.9.5")
    implementation("org.testcontainers:postgresql:${property("testContainersVersion")}") //to run in integration tests or locally without dependencies
    implementation("org.liquibase:liquibase-core")

    //liquibase plugin
    liquibaseRuntime("org.liquibase:liquibase-core:3.5.5")
    liquibaseRuntime("ch.qos.logback:logback-core:1.2.3")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.3")
    liquibaseRuntime("org.postgresql:postgresql:42.2.12")

    //Tests
    testImplementation("org.codehaus.groovy:groovy:3.0.7")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.spockframework:spock-core:${property("spockVersion")}")
    testImplementation("org.spockframework:spock-spring:${property("spockVersion")}")
    testImplementation("org.testcontainers:spock:${property("testContainersVersion")}")
    testImplementation("org.testcontainers:kafka:${property("testContainersVersion")}")
    testImplementation("org.springframework.cloud:spring-cloud-contract-wiremock:2.1.5.RELEASE") //should use default from BOM with ${springContractVersion}, but fails tests, thus downgraded
}

tasks {
    test {
        useJUnitPlatform()
        testLogging.showExceptions = true
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }
}

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "changeLogFile" to "$projectDir/db/changelog/db.changelog-master.xml",
                "url" to System.getProperty("liquibase.url"),
                "logLevel" to "debug"
        )
    }
    runList = "main"
}
