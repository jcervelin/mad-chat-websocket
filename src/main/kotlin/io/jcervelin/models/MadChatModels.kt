package io.jcervelin.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Long,
    val user: String,
    val content: String,
    val timestamp: Long
)

@Serializable
data class MessageRequest(
    val user: String,
    val content: String,
)

class ChatRoom {
    private var nextId: Long = 1
    private val messages = mutableListOf<Message>()

    fun addMessage(user: String, content: String, timestamp: Long): Message {
        val username = when (user) {
            "" -> "Anonymous"
            else -> user
        }
        val message = Message(nextId++, username, content, timestamp)
        messages.add(message)
        return message
    }

    fun messages(): List<Message> = messages.toList()
}

class History(private val history: LinkedHashMap<String, String>) {

    fun add(content: String, alteredContent: String) {
        history["Original: '${content}'"] = "Transformed: '${alteredContent}'"
    }

    fun chatHistory() = history.entries.map {
        """
            ${it.key}\n
            ${it.value}
        """.trimIndent()
    }.joinToString { "\n\n" }
}
