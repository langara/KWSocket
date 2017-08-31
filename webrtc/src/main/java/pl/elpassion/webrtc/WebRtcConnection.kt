package pl.elpassion.webrtc

import pl.elpassion.iot.api.Connection

class WebRtcConnection(val address: String, private val client: WebRtcClient) : Connection {

    override fun send(message: String) {
        client.transmit(address, message)
    }

    override fun close() {
        client.closeConnection(address)
    }
}