package pl.elpassion.webrtc

import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.VideoRenderer

interface WebRtcClient {

    var localRender: VideoRenderer.Callbacks?
    var remoteRender: VideoRenderer.Callbacks?

    interface ConnectionListener {
        fun onConnecting(remoteUser: String)
        fun onConnected(remoteUser: String)
        fun onDisconnected(remoteUser: String)
        fun onMessage(remoteUser: String, message: String)
        fun onDebug(message: String)
    }

    fun init(mediaStream: MediaStream)
    fun connect(remoteUser: String)
    fun acceptConnection(remoteUser: String)
    fun closeConnection(remoteUser: String)
    fun closeAllConnections()
    fun transmit(remoteUser: String, message: String)
    fun getVideoConstraints(): MediaConstraints
    fun getAudioConstraints(): MediaConstraints
}