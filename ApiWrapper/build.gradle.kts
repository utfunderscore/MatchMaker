plugins {
    id("java")
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.readutf.matchmaker"
version = getGitCommitNumber()

repositories {
    mavenCentral()

    maven {
        url = uri("https://oss.sonatype.org/content/repositories/")
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.readutf.matchmaker"
            artifactId = "api-wrapper"
            version = getGitCommitNumber()


            from(components["java"])
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":Shared"))
    implementation("com.squareup.retrofit2:retrofit:+")
    implementation("org.java-websocket:Java-WebSocket:1.5.6")
    implementation("com.google.code.gson:gson:2.10.1")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.7")


}

tasks.test {
    useJUnitPlatform()
}


fun getGitCommitNumber(): String {


    val stdOut = ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()
        .inputStream
        .bufferedReader()
        .readText()


    var result = stdOut.trim()
    println("result: $result")
    return result
}