package io.jcervelin

import io.jcervelin.models.ChatRoom
import io.jcervelin.models.History
import io.jcervelin.plugins.configureClientSerialization
import io.jcervelin.plugins.configureRouting
import io.jcervelin.plugins.configureSerialization
import io.jcervelin.services.LRUCache
import io.jcervelin.services.OpenAIClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module)
        .start(wait = true)
}

val j = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    prettyPrint = true
    isLenient = true
    encodeDefaults = true
}
val chatRoom = ChatRoom()
val history = History(LRUCache(30))
val httpClient = HttpClient(CIO) {
    configureClientSerialization()
}
private val openAIClient = OpenAIClient(apiKey = System.getenv("OPEN_AI_KEY") ?: "MISSING_KEY", history, httpClient)
inline fun <reified T> T.toJson(): String {
    return j.encodeToString(serializer(), this)
}

fun Application.module() {
    configureSerialization(j)
    configureRouting(chatRoom, openAIClient, history, clock = Clock.fixed(Instant.now(), ZoneId.systemDefault()))
}
