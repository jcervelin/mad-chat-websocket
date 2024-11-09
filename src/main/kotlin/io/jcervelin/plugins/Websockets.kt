package io.jcervelin.plugins

import io.jcervelin.chatRoom
import io.jcervelin.models.ChatRoom
import io.jcervelin.models.History
import io.jcervelin.models.Message
import io.jcervelin.models.MessageRequest
import io.jcervelin.services.AIClient
import io.jcervelin.services.sendMessageService
import io.jcervelin.toObject
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.time.Clock
import java.time.Instant
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(
    chatRoom: ChatRoom,
    openAIClient: AIClient,
    history: History,
    clock: Clock
) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val messageResponseFlow = MutableSharedFlow<Message>()
        val sharedFlow = messageResponseFlow.asSharedFlow()

        webSocket("/messages") {
            sendAllMessages()

            val job = launch {
                sharedFlow.collect { message ->
                    println("Launch: $message")
                    sendSerialized(message)
                }
            }

            runCatching {
                incoming.consumeEach {
                    when (it) {
                        is Frame.Text -> {
                            val messageRequest = it.readText().toObject<MessageRequest>()
                            log.info("messageRequest: $messageRequest")
                            val messageResponse = sendMessageService(
                                messageRequest,
                                chatRoom,
                                openAIClient,
                                history,
                                Instant.now(clock).toEpochMilli()
                            )
                            messageResponseFlow.emit(messageResponse)
                        }

                        else -> Unit
                    }
                }
            }.onFailure { ex ->
                log.error("Error while calling /sendMessage.", ex)
            }.also {
                job.cancel()
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllMessages() = chatRoom.messages().forEach { sendSerialized(it) }
