package io.jcervelin.plugins

import io.jcervelin.j
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

private fun HttpClientConfig<HttpClientEngineConfig>.configureClientSerialization() {
    install(ContentNegotiation) {
        json(j)
    }
}
