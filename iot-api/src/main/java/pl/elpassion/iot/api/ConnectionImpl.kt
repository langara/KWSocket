package pl.elpassion.iot.api

import org.java_websocket.WebSocket

class ConnectionImpl(val socket: WebSocket) : Connection {
    override fun close() = socket.close()
    override fun send(message: String) = socket.send(message)
}