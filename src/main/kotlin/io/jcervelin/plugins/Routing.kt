package io.jcervelin.plugins

import io.jcervelin.models.ChatRoom
import io.jcervelin.models.History
import io.jcervelin.models.MessageRequest
import io.jcervelin.services.AIClient
import io.jcervelin.services.fetchMessage
import io.jcervelin.services.sendMessageService
import io.jcervelin.toJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.swagger.codegen.v3.generators.html.StaticHtmlCodegen
import org.slf4j.LoggerFactory
import java.time.Clock
import java.time.Instant

private val logger = LoggerFactory.getLogger("routing")

fun Application.configureRouting(
    chatRoom: ChatRoom,
    openAIClient: AIClient,
    history: History,
    clock: Clock
) {
    routing {
        staticResources("/", "static")

        post("/sendMessage") {
            runCatching {
                val messageRequest = call.receive<MessageRequest>()
                sendMessageService(messageRequest, chatRoom, openAIClient, history, Instant.now(clock).toEpochMilli())
            }.fold(onSuccess = {
                call.respond(it)
                logger.info(it.toJson())
            }, onFailure = { ex ->
                run {
                    call.respond(HttpStatusCode.InternalServerError, "error")
                    logger.error("Error while calling /sendMessage.", ex)
                }
            })
        }

        get("/messages") {
            val lastMessageId = call.request.queryParameters["lastMessageId"]
            runCatching {
                fetchMessage(lastMessageId?.toLong() ?: 0, chatRoom).toList()
            }.fold(onSuccess = {
                logger.info(it.toJson())
                call.respond(it)
            }, onFailure = { ex ->
                run {
                    logger.error("Error while calling /sendMessage.", ex)
                    call.respond(HttpStatusCode.InternalServerError, "error")
                }
            })
        }

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml") {
            version = "4.15.5"
        }
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml") {
            codegen = StaticHtmlCodegen()
        }
    }
}
