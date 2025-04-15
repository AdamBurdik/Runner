plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.0"
}

group = "me.adamix.runner"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("ch.qos.logback:logback-classic:1.5.16")

    implementation(files("libs/JFramework-1.1.jar"))

    implementation("uk.co.electronstudio.jaylib:jaylib:5.5.+")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    testCompileOnly("org.projectlombok:lombok:1.18.36")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "me.adamix.runner.Runner"
        )
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    archiveVersion.set(project.version.toString())
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}