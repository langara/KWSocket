package pl.elpassion.iotguard.api

import io.reactivex.Observable

interface Socket : AutoCloseable {
    fun send(message: String)
}

sealed class Event(open val source: Socket? = null)

data class Open(override val source: Socket? = null) : Event(source)

data class Close(val code: Int, override val source: Socket? = null) : Event(source)

data class Message(val message: String, override val source: Socket? = null) : Event(source)

interface Endpoint : AutoCloseable {
    val connections: List<Socket> // client will have at most one. server can have many
    val events : Observable<Event>
    val messages : Observable<String> get() = events.ofType(Message::class.java).map { it.message }

    fun sendToAll(message: String) {
        val con = connections
        synchronized(con) {
            for (c in con) {
                c.send(message)
            }
        }
    }
}

interface Client : Endpoint {
    fun connect()
}

interface Server : Endpoint

