plugins {
    `java-library`
    `hypo-java`
    `hypo-module`
    `hypo-publish`
}

dependencies {
    compileOnlyApi(libs.annotations)

    api(projects.hypoModel)
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "dev.denwav.hypo.core"
        )
    }
}

hypoJava {
    javadocLibs.add(libs.annotations)
    javadocProjects.add(projects.hypoModel)
}

hypoPublish {
    component = components.named("java")
}
