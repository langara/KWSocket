package pl.mareklangiewicz.kws

import io.reactivex.Observable
import java.lang.Exception

interface Connection : AutoCloseable {
    fun send(message: String)
}

sealed class Event(open val source: Connection? = null)

data class Open(override val source: Connection? = null) : Event(source)

data class Close(val code: Int, override val source: Connection? = null) : Event(source)

data class Message(val message: String, override val source: Connection? = null) : Event(source)

data class Error(val exception: Exception, override val source: Connection? = null) : Event(source)

object Start : Event()

interface Endpoint : AutoCloseable {
    val connections: List<Connection> // client will have at most one. server can have many
    val eventS: Observable<Event>
}

val Endpoint.messageS: Observable<String> get() = eventS.ofType(Message::class.java).map(Message::message)

fun Endpoint.send(message: String) = connections.forEach { it.send(message) }

interface Client : Endpoint {
    fun connect(address: String)
}

interface Server : Endpoint {
    fun start()
}

