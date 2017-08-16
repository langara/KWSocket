package pl.elpassion.iotguard.api

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


class ServerImpl : Server {

    private val eventsSubject = PublishSubject.create<Event>()

    override val events: Observable<Event> = eventsSubject.hide()

    private var server: WSServer? = null

    override val connections: List<Socket>
        get() = server?.connections()?.map { SocketImpl(it) } ?: emptyList()

    override fun start(socketPort: Int) { close(); server = WSServer(socketPort).apply { start() } }
    override fun close() { server?.stop(); server = null }

    private inner class WSServer(socketPort: Int) : WebSocketServer(InetSocketAddress(socketPort)) {
        override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) = eventsSubject.onNext(Open(SocketImpl(conn)))
        override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) = eventsSubject.onNext(Close(code, SocketImpl(conn)))
        override fun onMessage(conn: WebSocket, message: String) = eventsSubject.onNext(Message(message, SocketImpl(conn)))
        override fun onError(conn: WebSocket?, exception: Exception) = eventsSubject.onNext(Error(exception))
        override fun onStart() = eventsSubject.onNext(Start)
    }
}