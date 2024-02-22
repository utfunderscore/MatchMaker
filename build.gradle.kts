import java.lang.ProcessBuilder.Redirect

plugins {
    id("java")
    id("maven-publish")
}

group = "com.readutf.matchmaker"
version = getGitCommitNumber()

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Shared"))

    //lombok
    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor( "org.projectlombok:lombok:1.18.30")

    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor( "org.projectlombok:lombok:1.18.30")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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