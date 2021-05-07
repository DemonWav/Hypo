plugins {
    `java-platform`
    `hypo-publish`
}

hypoPublish {
    component.set(components.named("javaPlatform"))
}

dependencies {
    constraints {
        api(projects.hypoAsm)
        api(projects.hypoAsm.hypoAsmHydrate)
        api(projects.hypoCore)
        api(projects.hypoHydrate)
        api(projects.hypoMappings)
        api(projects.hypoModel)
    }
}
