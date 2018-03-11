import java.net.URI

plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "pl.mareklangiewicz.kws.miniclient.MainKt"
}

repositories {
    jcenter()
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    implementation(Deps.kotlinStdlib)
    implementation(Deps.rxkotlin)
    implementation(project(":kwsocket"))
}
