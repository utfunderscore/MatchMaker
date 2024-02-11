plugins {
    id("java")
}

group = "com.readutf.matchmaker.client"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.netty:netty-all:4.1.106.Final")
    implementation(project(":Shared"))

    implementation("org.jetbrains:annotations:24.0.0")

    //lombok
    compileOnly ("org.projectlombok:lombok:1.18.30")
    annotationProcessor( "org.projectlombok:lombok:1.18.30")

    testCompileOnly ("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor( "org.projectlombok:lombok:1.18.30")
}

tasks.test {
    useJUnitPlatform()
}