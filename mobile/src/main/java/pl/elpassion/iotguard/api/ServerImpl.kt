package pl.elpassion.iotguard.api

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


class ServerImpl : Server {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private var server: WSServer? = null

    override val connections: List<Socket>
        get() = server?.connections()?.map { SocketImpl(it) } ?: emptyList()

    override fun start(socketPort: Int) {
        close()
        server = WSServer(socketPort).apply { start() }
    }

    override fun close() {
        server?.stop()
        server = null
    }

    private inner class WSServer(socketPort: Int) : WebSocketServer(InetSocketAddress(socketPort)) {
        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) = eventsRelay.accept(Open(SocketImpl(conn)))
        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) = eventsRelay.accept(Close(code, SocketImpl(conn)))
        override fun onMessage(conn: WebSocket, message: String) = eventsRelay.accept(Message(message, SocketImpl(conn)))
        override fun onError(conn: WebSocket?, exception: Exception) = eventsRelay.accept(Error(exception))
        override fun onStart() = eventsRelay.accept(Start)
    }
}