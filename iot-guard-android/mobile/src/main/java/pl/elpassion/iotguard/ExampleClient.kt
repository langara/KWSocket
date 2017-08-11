package pl.elpassion.iotguard

import org.java_websocket.client.WebSocketClient
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class ExampleClient(serverURI: URI) : WebSocketClient(serverURI) {

    private val logger by lazy { DI.provideLogger() }

    override fun onOpen(handshakedata: ServerHandshake) {
        logger.log("ExampleClient: opened connection")
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    override fun onMessage(message: String) {
        logger.log("ExampleClient: received: $message")
    }

    override fun onFragment(fragment: Framedata?) {
        logger.log("ExampleClient: received fragment: ${String(fragment!!.payloadData.array())}")
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        logger.log("ExampleClient: Connection closed by ${if (remote) "remote peer" else "us"}")
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        // if the error is fatal then onClose will be called additionally
    }

    companion object {
        fun launch(uri: URI = URI("ws://localhost:9999")) {
            val client = ExampleClient(uri)
            client.connect()
        }
    }
}