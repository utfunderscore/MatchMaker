import java.lang.ProcessBuilder.Redirect

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("maven-publish")
}

group = "com.readutf.matchmaker.client"
version = getGitCommitNumber()

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    
    api(project(":Shared"))

    implementation("io.netty:netty-all:4.1.106.Final")

    implementation("org.jetbrains:annotations:24.0.0")
    implementation("org.reflections:reflections:0.10.2")

    //lombok
    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor( "org.projectlombok:lombok:1.18.30")

    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor( "org.projectlombok:lombok:1.18.30")


}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.matchmaker"
            artifactId = "client"
            version = getGitCommitNumber()
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
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