package pl.mareklangiewicz.kws.api

import org.java_websocket.WebSocket

class WSConnection(val socket: WebSocket) : Connection {
    override fun disconnect() = socket.close()
    override fun send(message: String) {
        if (socket.isOpen) socket.send(message)
    }
}
