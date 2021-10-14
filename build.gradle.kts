plugins {
    java
    `maven-publish`
}

group = "ru.herobrine1st.fusion"
version = "3.0-SNAPSHOT"


repositories {
    mavenCentral()
    maven(url="https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:4.3.0_277")
    implementation("org.slf4j:slf4j-api:1.7.32")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group as String
            artifactId = rootProject.name
            version = rootProject.version as String

            from(components["java"])
        }
    }
}