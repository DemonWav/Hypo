plugins {
    `java-library`
    `hypo-java`
    `hypo-module`
    `hypo-publish`
    `hypo-test-scenario`
}

hypoTest {
    testDataProject = projects.hypoHydrate.hypoHydrateTestData
}

repositories {
    // for tests
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    implementation(projects.hypoCore)
    implementation(libs.jgrapht)

    testImplementation(projects.hypoTest)
}

tasks.compileTestJava {
    options.release = 21
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "dev.denwav.hypo.hydrate"
        )
    }
}

hypoJava {
    javadocLibs.add(libs.annotations)
    javadocLibs.add(libs.errorprone.annotations)
    javadocLibs.add(libs.jgrapht)
    javadocProjects.addAll(projects.hypoCore, projects.hypoModel)
}

hypoPublish {
    component = components.named("java")
}
