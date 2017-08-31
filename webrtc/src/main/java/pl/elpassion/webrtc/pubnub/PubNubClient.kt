package pl.elpassion.webrtc.pubnub

import android.app.Activity
import me.kevingleason.pnwebrtc.PnPeer
import me.kevingleason.pnwebrtc.PnRTCClient
import me.kevingleason.pnwebrtc.PnRTCListener
import org.json.JSONObject
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui
import pl.elpassion.webrtc.BuildConfig
import pl.elpassion.webrtc.WebRtcClient

class PubNubClient(private val activity: Activity,
                   private val username: String,
                   private val listener: WebRtcClient.ConnectionListener) : WebRtcClient {

    override var localRender: VideoRenderer.Callbacks? = null
    override var remoteRender: VideoRenderer.Callbacks? = null

    private val client by lazy { PnRTCClient(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY, username) }

    override fun init(mediaStream: MediaStream) = client.run {
        attachRTCListener(RtcListener())
        attachLocalMediaStream(mediaStream)
        listenOn(username)
        setMaxConnections(5)
    }

    override fun connect(remoteUser: String) {
        client.connect(remoteUser, true)
    }

    override fun acceptConnection(remoteUser: String) {
        client.connect(remoteUser, false)
    }

    override fun closeConnection(remoteUser: String) {
        client.closeConnection(remoteUser)
    }

    override fun closeAllConnections() {
        client.closeAllConnections()
    }

    override fun transmit(remoteUser: String, message: String) {
        client.transmit(remoteUser, JSONObject().apply { put("message", message) })
    }

    override fun getVideoConstraints(): MediaConstraints = client.videoConstraints()

    override fun getAudioConstraints(): MediaConstraints = client.audioConstraints()

    private inner class RtcListener : PnRTCListener() {

        override fun onLocalStream(localStream: MediaStream) {
            super.onLocalStream(localStream)
            activity.runOnUiThread {
                if (localStream.videoTracks.isNotEmpty()) {
                    localStream.videoTracks[0].addRenderer(VideoRenderer(localRender))
                }
            }
        }

        override fun onAddRemoteStream(remoteStream: MediaStream, peer: PnPeer) {
            super.onAddRemoteStream(remoteStream, peer)
            activity.runOnUiThread {
                listener.onConnected(peer.id)
                if (remoteStream.videoTracks.isNotEmpty()) {
                    remoteStream.videoTracks[0].addRenderer(VideoRenderer(remoteRender))
                }
                updateRenderer(remoteRender, 0, 0, 100, 100, false)
            }
        }

        private fun updateRenderer(renderer: VideoRenderer.Callbacks?,
                                   x: Int, y: Int, width: Int, height: Int, mirror: Boolean) {
            val scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL
            VideoRendererGui.update(renderer, x, y, width, height, scalingType, mirror)
        }

        override fun onMessage(peer: PnPeer, message: Any?) {
            super.onMessage(peer, message)
            if (message !is JSONObject) return
            if (message.has("message")) {
                listener.onMessage(peer.id, message.getString("message"))
            }
        }

        override fun onPeerStatusChanged(peer: PnPeer) {
            super.onPeerStatusChanged(peer)
            activity.runOnUiThread {
                when (peer.status) {
                    PnPeer.STATUS_CONNECTING -> listener.onConnecting(peer.id)
                    PnPeer.STATUS_CONNECTED -> listener.onConnected(peer.id)
                    PnPeer.STATUS_DISCONNECTED -> listener.onDisconnected(peer.id)
                }
            }
        }
    }
}