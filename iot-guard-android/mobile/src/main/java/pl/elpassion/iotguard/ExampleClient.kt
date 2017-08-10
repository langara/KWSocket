package pl.elpassion.iotguard

import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.framing.Framedata
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class ExampleClient : WebSocketClient {

    constructor(serverUri: URI, draft: Draft) : super(serverUri, draft) {}

    constructor(serverURI: URI) : super(serverURI) {}

    override fun onOpen(handshakedata: ServerHandshake) {
        println("opened connection")
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    override fun onMessage(message: String) {
        println("received: " + message)
    }

    override fun onFragment(fragment: Framedata?) {
        println("received fragment: " + String(fragment!!.payloadData.array()))
    }

    override fun onClose(code: Int, reason: String, remote: Boolean) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        println("Connection closed by " + if (remote) "remote peer" else "us")
    }

    override fun onError(ex: Exception) {
        ex.printStackTrace()
        // if the error is fatal then onClose will be called additionally
    }

    companion object {

        fun launch(uri: URI = URI("ws://localhost:9999")) {
            val client = ExampleClient(uri, Draft_6455()) // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
            client.connect()
        }
    }

}