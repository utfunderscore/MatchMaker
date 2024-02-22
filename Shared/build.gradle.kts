import java.lang.ProcessBuilder.Redirect

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

group = "com.readutf.matchmaker"
version = getGitCommitNumber()

repositories {
    mavenCentral()
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.matchmaker"
            artifactId = "shared"
            version = getGitCommitNumber()
            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.apache.logging.log4j:log4j-api:2.7")
    implementation("org.apache.logging.log4j:log4j-core:2.7")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.7")

    compileOnly("org.jetbrains:annotations:24.0.0")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")

// https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:5.0.0.Alpha2")

    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor( "org.projectlombok:lombok:1.18.30")

    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor( "org.projectlombok:lombok:1.18.30")
}

tasks.test {
    useJUnitPlatform()
}

// Change shadow jar name
tasks.shadowJar {
    archiveFileName.set("Shared.jar")
}

fun getGitCommitNumber(): String {
    val stdOut = ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .redirectOutput(Redirect.PIPE)
        .start()
        .inputStream
        .bufferedReader()
        .readText()

    return stdOut.trim()
}