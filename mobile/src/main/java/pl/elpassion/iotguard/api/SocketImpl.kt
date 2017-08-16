package pl.elpassion.iotguard.api

import org.java_websocket.WebSocket

class SocketImpl(val socket: WebSocket) : Socket {
    override fun close() = socket.close()
    override fun send(message: String) = socket.send(message)
}