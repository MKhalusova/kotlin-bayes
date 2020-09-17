import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"

}
group = "me.mariakhalusova"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("com.beust:klaxon:5.0.1")
    implementation("com.google.guava:guava:29.0-jre")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}