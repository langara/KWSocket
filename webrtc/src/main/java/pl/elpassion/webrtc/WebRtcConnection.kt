package pl.elpassion.webrtc

import pl.elpassion.iot.api.Connection

class WebRtcConnection(val address: String, private val manager: WebRtcManager) : Connection {

    override fun send(message: String) {
        manager.transmit(address, message)
    }

    override fun close() {
        manager.closeConnection(address)
    }
}