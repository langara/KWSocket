package pl.elpassion.iotguard.api

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class ClientImpl : Client {

    private val eventsSubject = PublishSubject.create<Event>()

    override val events : Observable<Event> = eventsSubject.hide()

    private var client : WebSocketClient? = null

    override val connections: List<Socket>
        get() = client?.connection?.let { listOf(SocketImpl(it)) } ?: emptyList()

    override fun connect(serverURI: String) { close(); client = WSClient(serverURI).apply { connect() } }

    override fun close() { client?.closeBlocking(); client = null }

    private inner class WSClient(serverURI: String) : WebSocketClient(URI(serverURI)) {
        override fun onOpen(handshakedata: ServerHandshake?) = eventsSubject.onNext(Open())
        override fun onClose(code: Int, reason: String?, remote: Boolean) = eventsSubject.onNext(Close(code))
        override fun onMessage(message: String) = eventsSubject.onNext(Message(message))
        override fun onError(exception: Exception) = eventsSubject.onNext(Error(exception))
    }
}