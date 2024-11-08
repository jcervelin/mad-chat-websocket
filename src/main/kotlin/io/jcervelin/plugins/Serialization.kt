package io.jcervelin.plugins

import io.jcervelin.j
import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

fun Application.configureSerialization(j: Json) {
    install(ContentNegotiation) {
        json(j)
    }
}

fun HttpClientConfig<*>.configureClientSerialization() {
    install(ClientContentNegotiation) {
        json(j)
    }
}
