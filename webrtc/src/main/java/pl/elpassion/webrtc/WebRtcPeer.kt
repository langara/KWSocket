package pl.elpassion.webrtc

import android.app.Activity
import android.opengl.GLSurfaceView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.elpassion.iot.api.*

class WebRtcPeer(activity: Activity, surfaceView: GLSurfaceView, username: String) : Client, Server {

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

    }
    private val manager = WebRtcManager(activity, surfaceView, listener, username)

    override val connections: ArrayList<WebRtcConnection> = ArrayList()

    override fun connect(address: String) {
        manager.service.callUser(address) {
            manager.client.connect(it)
        }
        // TODO: does it make sense?
    }

    override fun close() {
        manager.client.closeAllConnections()
    }

    override fun start() {
        manager.service.startListening {
            manager.client.acceptConnection(it)
        }
    }
}