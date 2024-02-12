plugins {
    id("java")
}

group = "com.readutf.matchmaker"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

// https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.1.106.Final")
    implementation(project(":Shared"))
    implementation("org.apache.logging.log4j:log4j-api:2.7")
    implementation("org.apache.logging.log4j:log4j-core:2.7")
    implementation("io.netty:netty-tcnative:2.0.1.Final")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("io.javalin:javalin:6.0.1") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("com.google.code.gson:gson:2.10.1")
    //add reflections
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.7")


}

tasks.test {
    useJUnitPlatform()
}