val junitJupiterVersion: String by project

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    jacoco
}

group = "co.uk.collcomputing"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    create("integrationTest") {
        kotlin {
            compileClasspath += main.get().output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath
        }
        resources.srcDir("src/test/resources")
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.test {
    useJUnitPlatform()
}

task<Test>("integrationTest") {
    useJUnitPlatform()
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.jacocoTestReport {
    dependsOn(":test")
}