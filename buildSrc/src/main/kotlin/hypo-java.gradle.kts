import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.notExists

plugins {
    `java-library`
}

val hypoJava = extensions.create("hypoJava", HypoJavaExtension::class)

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 11
}

afterEvaluate {
    tasks.jar {
        for (projDep in hypoJava.jdkVersionProjects.get()) {
            val task = project(projDep.path).sourceSets.main.map { it.output }
            dependsOn(task)

            from(task)
        }
    }

    tasks.named("sourcesJar", Jar::class) {
        for (projDep in hypoJava.jdkVersionProjects.get()) {
            val proj = project(projDep.path)

            from(proj.sourceSets.main.map { it.allSource })
        }
    }

    // javadoc doesn't like that static.javadoc.io redirects, so we'll manually copy the
    // {element,package}-list for it so it doesn't complain
    val elementLists = layout.buildDirectory.dir("javadocElementLists")
    val javadocElementList by tasks.registering {
        inputs.property("libs", hypoJava.javadocLibs)
        outputs.dir(elementLists)

        doLast {
            val client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()

            val outDir = elementLists.get().asFile.toPath()
            val base = "https://static.javadoc.io"
            hypoJava.javadocLibs.get().forEach { m ->
                val types = listOf("element", "package")
                var response: HttpResponse<*>? = null
                for (type in types) {
                    val filePath = "${m.module.group}/${m.module.name}/${m.versionConstraint}/$type-list"
                    val url = "$base/$filePath"
                    val outFile = outDir.resolve(filePath)
                    outFile.parent.createDirectories()

                    val request = HttpRequest.newBuilder().GET().uri(URI.create(url)).build()
                    response = client.send(request, HttpResponse.BodyHandlers.ofFile(outFile))
                    if (response.statusCode() == 200) {
                        break
                    }

                    outFile.deleteIfExists()
                }
                if (response == null || response.statusCode() != 200) {
                    throw Exception("Failed: $response")
                }
            }
        }
    }

    tasks.javadoc {
        dependsOn(javadocElementList)

        for (projDep in hypoJava.jdkVersionProjects.get()) {
            val proj = project(projDep.path)

            val sources = files(proj.sourceSets.main.map { it.allJava })
            source += sources.asFileTree
            classpath += sources
        }

        val packageListDir = elementLists.get().asFile.toPath()
        hypoJava.javadocLibs.get().forEach { m ->
            val base = "https://static.javadoc.io"
            val artifact = "${m.module.group}/${m.module.name}/${m.versionConstraint}"
            val packageDir = packageListDir.resolve(artifact)
            val url = "$base/$artifact"

            opt.linksOffline(url, packageDir.absolutePathString())
        }

        hypoJava.javadocProjects.get().forEach { p ->
            val javadocTask = project(p.path).tasks.javadoc
            dependsOn(javadocTask)

            val url = "$base/${p.group}/${p.name}/${p.version}"
            opt.linksOffline(url, javadocTask.get().destinationDir!!.absolutePath)
        }

        doLast {
            // a lot of tools still require a package-list file instead of element-list
            destinationDir!!.resolve("element-list").copyTo(destinationDir!!.resolve("package-list"))
        }
    }
}
