package io.jcervelin.plugins

import io.jcervelin.chatRoom
import io.jcervelin.history
import io.jcervelin.j
import io.jcervelin.models.Message
import io.jcervelin.services.AIClient
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import junit.framework.TestCase.assertEquals
import kotlinx.serialization.json.Json
import java.time.*
import kotlin.test.Test
import io.ktor.client.plugins.websocket.WebSockets as ClientWebsockets


class WebsocketsKtTest {

    @Test
    fun testGetMessages() = testApplication {

        val fixedDateTime = ZonedDateTime.of(LocalDateTime.of(2020, 10, 10, 0, 0), ZoneOffset.UTC)

        application {
            configureSerialization(j)
            configureSockets(
                chatRoom = chatRoom, openAIClient = MockOpenAIClient(), history = history,
                clock = Clock.fixed(
                    Instant.from(fixedDateTime),
                    ZoneOffset.UTC
                )
            )
        }

        val client = createClient {
            install(ClientWebsockets)
        }

        val websocketClient = client
            .config {
                configureClientSerialization()
            }

        val request = Frame.Text(
            """
                {
                  "user": "Juliano",
                  "content": "Show time!"
                }
            """.trimIndent()
        )

        val expected = Message(
            id = 1,
            user = "Juliano",
            content = "Something funny and rude. Show time!",
            timestamp = 1602288000000
        )

        websocketClient.webSocket("/messages") {
            send(request)
            val textReceived = (incoming.receive() as? Frame.Text)?.readText() ?: ""
            val result = Json.decodeFromString<Message>(textReceived)
            assertEquals(expected, result)
        }
    }
}

class MockOpenAIClient : AIClient {
    override suspend fun getRudeResponse(userMessage: String): String {
        return "Something funny and rude. $userMessage"
    }
}
