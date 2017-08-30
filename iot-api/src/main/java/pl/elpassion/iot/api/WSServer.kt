package pl.elpassion.iot.api

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


class WSServer(private val port: Int) : Server {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private var server: WebSocketServerImpl? = null

    override val connections: List<Connection>
        get() = server?.connections()?.map { WSConnection(it) } ?: emptyList()

    override fun start() {
        close()
        server = WebSocketServerImpl(port).apply { start() }
    }

    override fun close() {
        server?.stop()
        server = null
    }

    private inner class WebSocketServerImpl(socketPort: Int) : WebSocketServer(InetSocketAddress(socketPort)) {
        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) = eventsRelay.accept(Open(WSConnection(conn)))
        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) = eventsRelay.accept(Close(code, WSConnection(conn)))
        override fun onMessage(conn: WebSocket, message: String) = eventsRelay.accept(Message(message, WSConnection(conn)))
        override fun onError(conn: WebSocket?, exception: Exception) = eventsRelay.accept(Error(exception))
        override fun onStart() = eventsRelay.accept(Start)
    }
}