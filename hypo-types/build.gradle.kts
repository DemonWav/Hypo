plugins {
    `java-library`
    `hypo-java`
    `hypo-test`
    `hypo-module`
    `hypo-publish`
}

dependencies {
    compileOnlyApi(libs.annotations)
}

tasks.jar {
    manifest {
        attributes(
            "Automatic-Module-Name" to "dev.denwav.hypo.types"
        )
    }
}

hypoJava {
    javadocLibs.add(libs.annotations)
    javadocLibs.add(libs.errorprone.annotations)
}

hypoPublish {
    component = components.named("java")
}

tasks.compileTestJava {
    options.release = 21
}

val typesExport = project(":types-export")
tasks.test {
    dependsOn(typesExport.tasks.named("buildTypesExport"))

//    systemProperty("hypo.interning.disabled", "true")
    val zipFile = typesExport.layout.buildDirectory.file("types-export/types-export.zip").get().asFile.absolutePath
    systemProperty("hypo.types.zip", zipFile)
}
