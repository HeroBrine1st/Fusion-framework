plugins {
    application
}

group = "ru.herobrine1st.fusion"
version = "3.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url="https://m2.dv8tion.net/releases")
}

application {
    mainClass.set("ru.herobrine1st.fusion.Fusion")
}

dependencies {
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.reflections:reflections:0.9.12")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}