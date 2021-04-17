plugins {
    groovy
    application
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:2.5.13")
}

application {
    mainClass.set("it.App")
}
