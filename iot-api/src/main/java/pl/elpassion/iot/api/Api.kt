package pl.elpassion.iot.api

import io.reactivex.Observable
import java.lang.Exception

interface Connection {
    fun send(message: String)
    fun disconnect()
}

sealed class Event(open val source: Connection? = null)

data class Open(override val source: Connection? = null) : Event(source)

data class Close(val code: Int, override val source: Connection? = null) : Event(source)

data class Message(val message: String, override val source: Connection? = null) : Event(source)

data class Error(val exception: Exception, override val source: Connection? = null) : Event(source)

data class DebugMessage(val message: String) : Event()

object Start : Event()

interface Endpoint {
    val connections: List<Connection> // client will have at most one. server can have many
    val events: Observable<Event>
    fun disconnect()
}

val Endpoint.messages: Observable<String> get() = events.ofType(Message::class.java).map { it.message }

fun Endpoint.send(message: String) = connections.forEach { it.send(message) }

interface Client : Endpoint {
    fun connect(address: String)
}

interface Server : Endpoint {
    fun start()
}

