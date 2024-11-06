package io.jcervelin.services

import io.jcervelin.models.ChatRoom
import io.jcervelin.models.History
import io.jcervelin.models.Message
import io.jcervelin.models.MessageRequest

suspend fun sendMessageService(messageRequest: MessageRequest, chatRoom: ChatRoom,
                       openAIClient: AIClient, history: History, timestamp: Long
): Message {

    val alteredContent = openAIClient.getRudeResponse(messageRequest.content)

    history.add(content = messageRequest.content, alteredContent = alteredContent)

    return chatRoom.addMessage(messageRequest.user, alteredContent, timestamp)
}

fun fetchMessage(lastMessageId: Long, chatRoom: ChatRoom): List<Message> =
    chatRoom.messages().dropWhile {
        it.id <= lastMessageId
    }
