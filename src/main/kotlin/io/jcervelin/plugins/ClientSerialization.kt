package io.jcervelin.plugins

import io.jcervelin.j
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun HttpClientConfig<*>.configureClientSerialization() {
    install(ContentNegotiation) {
        json(j)
    }
}
