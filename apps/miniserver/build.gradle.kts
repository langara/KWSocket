import java.net.URI

@Suppress("UNCHECKED_CAST")
val deps = rootProject.ext.properties["deps"] as Map<String, Map<String, String>>
// TODO: find in kotlin-dsl repo proper syntax to use ext properties in build.gradle.kts


plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.kws.miniserver.MainKt"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    implementation(deps["kotlinStdlib"]!!)
    implementation(deps["rxjava"]!!)
    implementation(project(":kwsocket"))
}
