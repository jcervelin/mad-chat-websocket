package io.jcervelin.services

import io.jcervelin.j
import io.jcervelin.models.History
import io.jcervelin.plugins.configureClientSerialization
import io.jcervelin.plugins.configureSerialization
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals


class OpenAIClientTest {

    @Ignore
    fun test()  {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""
                        
                    """.trimIndent()),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }


            val openAIClient = OpenAIClient("apiKey", History(LinkedHashMap()), HttpClient(mockEngine))
            val rudeResponse = openAIClient.getRudeResponse("Something nice")
            rudeResponse

        }




        val openAIChatResponse = OpenAIChatResponse(
            listOf(
                OpenAIChatChoice(
                    OpenAIChatMessage(
                        "someRole",
                        "some rude message"
                    )
                )
            )
        )


//        val rudeResponse = OpenAIClient("someApiKey", History(LRUCache(1)), client)
//            .getRudeResponse("Something nice")

//        assertEquals("some rude message", rudeResponse)
    }
}