package io.jcervelin.plugins

import io.jcervelin.models.Priority
import io.jcervelin.models.Task
import io.jcervelin.models.TaskRepository
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.collections.ArrayList
import kotlin.time.Duration.Companion.seconds


fun Application.configureSockets() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val sessions =
            Collections.synchronizedList<WebSocketServerSession>(ArrayList())

        webSocket("/tasks") {
            sendAllTasks()
            close(CloseReason(CloseReason.Codes.NORMAL, "All done"))
        }

        webSocket("/tasks2") {
            sessions.add(this)
            sendAllTasks()

            incoming.consumeEach {
                val newTask = receiveDeserialized<Task>()
                TaskRepository.addTask(newTask)
                sessions.addTask(newTask)
            }
//            while(true) {
//                val newTask = receiveDeserialized<Task>()
//                TaskRepository.addTask(newTask)
//                sessions.addTask(newTask)
//            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllTasks() {
    for (task in TaskRepository.allTasks()) {
        sendSerialized(task)
        delay(1000)
    }
}

private suspend fun List<WebSocketServerSession>.addTask(newTask: Task) = this.forEach { it.sendSerialized(newTask)
}