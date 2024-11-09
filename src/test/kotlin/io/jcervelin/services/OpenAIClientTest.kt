package io.jcervelin.services

import io.jcervelin.models.History
import io.jcervelin.plugins.configureClientSerialization
import io.jcervelin.toJson
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


class OpenAIClientTest {

    @Test
    fun `Should call openai api and return modified message`() {
        runBlocking {
            val openAIChatResponse =
                OpenAIChatResponse(listOf(OpenAIChatChoice(OpenAIChatMessage("", "Something rude"))))
            val mockEngine = MockEngine {
                respond(
                    content = ByteReadChannel(openAIChatResponse.toJson()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val httpClient = HttpClient(mockEngine).config {
                configureClientSerialization()
            }

            val openAIClient = OpenAIClient("apiKey", History(LinkedHashMap()), httpClient)
            val rudeResponse = openAIClient.getRudeResponse("Something nice")

            assertEquals("Something rude", rudeResponse)
        }
    }
}
