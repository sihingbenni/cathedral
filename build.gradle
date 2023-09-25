plugins {
    id("java")
}

group = "de.student"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation name: 'cathedral-game-1.3'
    implementation "com.discord4j:discord4j-core:3.2.3"

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.test {
    useJUnitPlatform()
}
