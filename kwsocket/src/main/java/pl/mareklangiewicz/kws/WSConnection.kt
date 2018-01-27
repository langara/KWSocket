package pl.mareklangiewicz.kws

import org.java_websocket.WebSocket

class WSConnection(private val socket: WebSocket) : Connection {
    override fun close() = socket.close()
    override fun send(message: String) {
        if (socket.isOpen) socket.send(message)
    }
}
