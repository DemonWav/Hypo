plugins {
    java
    `hypo-java`
    `hypo-test-scenario-data`
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 21
}
