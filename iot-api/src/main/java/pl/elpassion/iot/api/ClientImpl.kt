package pl.elpassion.iot.api

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class ClientImpl : Client {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private var client: WebSocketClient? = null

    override val connections: List<Connection>
        get() = client?.connection?.let { listOf(ConnectionImpl(it)) } ?: emptyList()

    override fun connect(serverURI: String) {
        close()
        client = WSClient(serverURI).apply { connect() }
    }

    override fun close() {
        client?.closeBlocking()
        client = null
    }

    private inner class WSClient(serverURI: String) : WebSocketClient(URI(serverURI)) {
        override fun onOpen(handshakedata: ServerHandshake?) = eventsRelay.accept(Open())
        override fun onClose(code: Int, reason: String?, remote: Boolean) = eventsRelay.accept(Close(code))
        override fun onMessage(message: String) = eventsRelay.accept(Message(message))
        override fun onError(exception: Exception) = eventsRelay.accept(Error(exception))
    }
}