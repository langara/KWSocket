package pl.mareklangiewicz.kws

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class WSClient : Client {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private var client: WebSocketClient? = null

    override val connections: List<Connection>
        get() = client?.connection?.let { listOf(WSConnection(it)) } ?: emptyList()

    override fun connect(address: String) {
        disconnect()
        client = WSClient(address).apply { connect() }
    }

    override fun disconnect() {
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
