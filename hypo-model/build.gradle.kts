plugins {
    `java-library`
    `hypo-java`
    `hypo-test`
    `hypo-module`
    `hypo-publish`
}

dependencies {
    compileOnlyApi(libs.annotations)

    api(projects.hypoTypes)
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "dev.denwav.hypo.model"
        )
    }
}

hypoJava {
    javadocLibs.add(libs.annotations)

    javadocProjects.add(projects.hypoTypes)
}

hypoPublish {
    component = components.named("java")
}

tasks.compileTestJava {
    options.release = 21
}
