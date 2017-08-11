package pl.elpassion.iotguard.api

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class ClientImpl(serverURI: URI) : Client {

    constructor(serverURI: String) : this(URI(serverURI))

    private val eventsSubject = PublishSubject.create<Event>()

    override val events : Observable<Event> = eventsSubject.hide()

    val client = object : WebSocketClient(serverURI) {
        override fun onOpen(handshakedata: ServerHandshake?) = eventsSubject.onNext(Open())
        override fun onClose(code: Int, reason: String?, remote: Boolean) = eventsSubject.run { onNext(Close(code)); onComplete() }
        override fun onMessage(message: String) = eventsSubject.onNext(Message(message))
        override fun onError(exception: Exception) = eventsSubject.onError(exception)
    }

    override val connections: List<Socket>
        get() = client.connection?.let { listOf(SocketImpl(it)) } ?: emptyList()

    override fun connect() = client.connect()

    override fun close() = client.close()

}