package io.jcervelin.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json



fun Application.configureSerialization(j: Json) {
    install(ContentNegotiation) {
        json(j)
    }
}
