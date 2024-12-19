plugins {
    `java-library`
    `hypo-java`
    `hypo-module`
    `hypo-publish`
    `hypo-test-scenario`
}

hypoTest {
    testDataProject = projects.hypoAsm.hypoAsmTestData
}

repositories {
    // for tests
    maven("https://maven.quiltmc.org/repository/release/")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.asm)
    api(libs.slf4j.api)

    api(projects.hypoCore)
    api(projects.hypoModel)
    api(projects.hypoTypes)

    testImplementation(projects.hypoTest)
}

tasks.compileTestJava {
    options.release = 21
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "dev.denwav.hypo.asm"
        )
    }
}

hypoJava {
    javadocLibs.add(libs.annotations)
    javadocLibs.addAll(libs.bundles.asm)
    javadocLibs.add(libs.slf4j.api)
    javadocProjects.addAll(projects.hypoCore, projects.hypoModel, projects.hypoTypes)
}

hypoPublish {
    component = components.named("java")
}
