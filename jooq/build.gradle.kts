import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    id("org.jooq.jooq-codegen-gradle") version "3.19.6"
}

group = "com.senacor.academy.objpa"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    jooqCodegen("org.jooq:jooq-meta-extensions:3.19.6")

    // see https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.build-systems.starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:3.19.6") // Use latest jOOQ 3.19 to make use of Gradle-Plugin (there is no plugin for 3.18)
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.h2database:h2:2.2.224")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

jooq {
    configuration {
        generator {
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "src/main/resources/schema.sql"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "upper"
                    }
                }
            }

            target {
                packageName = "com.senacor.academy.objpa.jooq"
                directory = "src/main/java"
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
