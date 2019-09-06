/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package teamcity.slack.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.KotlinModule
import spark.Spark.*
import teamcity.slack.app.BuildState.Waiting

// TODO Return sensible http response content
fun main(args: Array<String>) {
    val builds = mutableListOf<Build>()

    port(getPort())
    get("/build") { req, _ ->
        builds
                .filter { it.state.name == req.queryParams("state") }
                .map { ObjectMapper().writeValueAsString(it) }
    }
    post("/build") { req, _ ->
        builds.add(Build(ObjectMapper()
                .registerModule(KotlinModule())
                .readValue(req.body(), BuildId::class.java).id, Waiting))
    }
    delete("/build") { req, _ ->
        builds.removeIf {
            it.id == ObjectMapper()
                    .registerModule(KotlinModule())
                    .readValue(req.body(), BuildId::class.java).id
        }
    }
}

data class Build(val id: String, val state: BuildState)

enum class BuildState {
    Waiting
}

@JacksonXmlRootElement
data class BuildId(val id: String)

fun getPort() = ProcessBuilder().environment()["PORT"]?.toInt() ?: 4567