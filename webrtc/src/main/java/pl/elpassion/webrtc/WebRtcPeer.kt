package pl.elpassion.webrtc

import android.app.Activity
import android.opengl.GLSurfaceView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.elpassion.iot.api.*

class WebRtcPeer(activity: Activity, surfaceView: GLSurfaceView, username: String, audioEnabled: Boolean = true) : Client, Server {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private val listener: WebRtcClient.ConnectionListener = object : WebRtcClient.ConnectionListener {
        override fun onConnecting(remoteUser: String) { }

        override fun onConnected(remoteUser: String) {
            val connection = WebRtcConnection(remoteUser, manager.client)
            connections.add(connection)
            eventsRelay.accept(Open(connection))
        }

        override fun onDisconnected(remoteUser: String) {
            val connection = connections.find { it.address == remoteUser }
            eventsRelay.accept(Close(0, connection))
            connections.remove(connection)
        }

        override fun onMessage(remoteUser: String, message: String) {
            val connection = connections.find { it.address == remoteUser } ?: WebRtcConnection(remoteUser, manager.client)
            eventsRelay.accept(Message(message, connection))
        }

        override fun onDebug(message: String) {
            eventsRelay.accept(DebugMessage(message))
        }
    }
    private val manager = WebRtcManager(activity, surfaceView, listener, username, audioEnabled)

    override val connections: ArrayList<WebRtcConnection> = ArrayList()

    override fun connect(address: String) {
        manager.streaming.callUser(address) {
            manager.client.connect(it)
        }
    }

    override fun disconnect() {
        manager.client.closeAllConnections()
    }

    override fun start() {
        manager.streaming.startListening {
            manager.client.acceptConnection(it)
        }
    }
}