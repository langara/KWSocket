package pl.elpassion.webrtc

import android.app.Activity
import android.opengl.GLSurfaceView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import pl.elpassion.iot.api.*
import pl.elpassion.webrtc.WebRtcManager.ConnectionListener


class WebRTCPeer(activity: Activity, surfaceView: GLSurfaceView, username: String) : Client, Server {

    private val eventsRelay = PublishRelay.create<Event>()

    override val events: Observable<Event> = eventsRelay.hide()

    private val listener: ConnectionListener = object : ConnectionListener {
        override fun onConnecting(remoteUser: String) { }

        override fun onConnected(remoteUser: String) {
            val connection = WebRTCConnection(remoteUser, manager)
            connections.add(connection)
            eventsRelay.accept(Open(connection))
        }

        override fun onDisconnected(remoteUser: String) {
            val connection = connections.find { it.address == remoteUser }
            eventsRelay.accept(Close(0, connection))
            connections.remove(connection)
        }

        override fun onMessage(remoteUser: String, message: String) {
            val connection = connections.find { it.address == remoteUser } ?: WebRTCConnection(remoteUser, manager)
            eventsRelay.accept(Message(message, connection))
        }

    }
    private val manager = WebRtcManager(activity, surfaceView, listener, username)

    override val connections: ArrayList<WebRTCConnection> = ArrayList()

    override fun connect(address: String) {
        manager.callUser(address) // TODO: does it make sense?
    }

    override fun close() {
        manager.cancelAllCalls()
    }

    override fun start() {
        manager.startListening()
    }
}