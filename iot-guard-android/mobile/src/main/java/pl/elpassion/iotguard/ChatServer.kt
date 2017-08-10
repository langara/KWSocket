package pl.elpassion.iotguard

import org.java_websocket.WebSocket
import org.java_websocket.WebSocketImpl
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress


class ChatServer(socketPort: Int = 80) : WebSocketServer(InetSocketAddress(socketPort)) {

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
        this.sendToAll("new connection: ${handshake.resourceDescriptor}")
        log("${conn.remoteSocketAddress.address.hostAddress} entered the room!")
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String, remote: Boolean) {
        this.sendToAll("$conn has left the room!")
        log("$conn has left the room!")
    }

    override fun onMessage(conn: WebSocket, message: String) {
        this.sendToAll(message)
        log("$conn: $message")
    }

    override fun onFragment(conn: WebSocket, fragment: Framedata) {
        log("received fragment: $fragment")
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    override fun onStart() {
        log("Server started!")
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.

     * @param text
     * *            The String to send across the network.
     * *
     * @throws InterruptedException
     * *             When socket related I/O errors occur.
     */
    fun sendToAll(text: String) {
        val con = connections()
        synchronized(con) {
            for (c in con) {
                c.send(text)
            }
        }
    }


    companion object {

        fun log(message: String) = println(message)

        fun launch(port: Int = 80) {
            WebSocketImpl.DEBUG = true
            ChatServer(port).run {
                start()
                log("ChatServer started on address: $address port: $port")
            }
        }
    }
}