package io.jcervelin.plugins

import io.jcervelin.chatRoom
import io.jcervelin.history
import io.jcervelin.j
import io.jcervelin.models.Message
import io.jcervelin.services.AIClient
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import junit.framework.TestCase.assertEquals
import java.time.*
import kotlin.test.Test

class RoutingKtTest {

    @Test
    fun testGetMessages() = testApplication {

        val fixedDateTime = ZonedDateTime.of(LocalDateTime.of(2020, 10, 10, 0, 0), ZoneOffset.UTC)

        application {
            configureSerialization(j)
            configureRouting(chatRoom = chatRoom, openAIClient = MockOpenAIClient(), history = history,
                clock = Clock.fixed(Instant.from(fixedDateTime),
                ZoneOffset.UTC)
            )
        }

        val httpClient = client
            .config {
                install(ContentNegotiation) {
                    json(j)
                }
            }


        httpClient
            .post("/sendMessage") {
            setBody(
                """
                {
                  "user": "Juliano",
                  "content": "Show time!"
                }
            """.trimIndent()
            )
            contentType(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)

        }

        val expected : List<Message> = listOf(Message(
            id = 1,
            content = "Something funny and rude. Show time!",
            user = "Juliano",
            timestamp = Instant.from(fixedDateTime).toEpochMilli()
        ))

        httpClient.get {
            url {
                path("messages")
                parameters.append("lastMessageId","0")
            }
            contentType(ContentType.Application.Json)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val response = body<List<Message>>()

            assertEquals(expected.size,1)
            assertEquals(expected, response)
        }
    }
}

class MockOpenAIClient : AIClient {
    override suspend fun getRudeResponse(userMessage: String): String {
        return "Something funny and rude. $userMessage"
    }
}
