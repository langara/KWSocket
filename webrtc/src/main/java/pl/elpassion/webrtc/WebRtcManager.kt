@file:Suppress("INACCESSIBLE_TYPE")

package pl.elpassion.webrtc

import android.app.Activity
import android.opengl.GLSurfaceView
import me.kevingleason.pnwebrtc.PnPeer
import me.kevingleason.pnwebrtc.PnRTCClient
import me.kevingleason.pnwebrtc.PnRTCListener
import org.json.JSONObject
import org.webrtc.*
import pl.elpassion.webrtc.pubnub.PubNubService

class WebRtcManager(private val activity: Activity,
                    surfaceView: GLSurfaceView,
                    private val listener: ConnectionListener,
                    private val username: String) {

    interface ConnectionListener {
        fun onConnecting(remoteUser: String)
        fun onConnected(remoteUser: String)
        fun onDisconnected(remoteUser: String)
        fun onMessage(remoteUser: String, message: String)
    }

    private val client by lazy { PnRTCClient(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY, username) }
    private val service: WebRtcService by lazy { PubNubService() }

    private var localRender: VideoRenderer.Callbacks? = null
    private var remoteRender: VideoRenderer.Callbacks? = null

    init {
        PeerConnectionFactory.initializeAndroidGlobals(
                activity, // Context
                true, // Audio Enabled
                true, // Video Enabled
                true, // Hardware Acceleration Enabled
                null) // Render EGL Context

        VideoRendererGui.setView(surfaceView, null)
        localRender = createVideoRenderer()
        remoteRender = createVideoRenderer()

        initClient()
    }

    fun transmit(username: String, message: String) {
        client.transmit(username, JSONObject().apply { put("message", message) })
    }

    fun startListening() {
        service.startListening(username) {
            client.connect(it, false)
        }
    }

    fun callUser(remoteUsername: String) {
        service.callUser(username, remoteUsername) {
            client.connect(it, true)
        }
    }

    fun cancelAllCalls() {
        client.closeAllConnections()
    }

    fun closeConnection(username: String) {
        client.closeConnection(username)
    }

    private fun initClient() = client.run {
        attachRTCListener(RtcListener())
        attachLocalMediaStream(createMediaStream())
        listenOn(username)
        setMaxConnections(5)
    }

    private fun createVideoRenderer() = VideoRendererGui.create(0, 0, 100, 100,
            VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false)

    private fun createMediaStream(): MediaStream {
        val factory = PeerConnectionFactory()
        return factory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID).apply {
            createVideoTrack(factory)?.let { addTrack(it) }
            addTrack(createAudioTrack(factory))
        }
    }

    private fun createVideoTrack(factory: PeerConnectionFactory): VideoTrack? {
        val cameraDevice = VideoCapturerAndroid.getNameOfBackFacingDevice()
        val capturer = VideoCapturerAndroid.create(cameraDevice)
        return if (capturer != null) {
            val localVideoSource = factory.createVideoSource(capturer, client.videoConstraints())
            factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        } else null
    }

    private fun createAudioTrack(factory: PeerConnectionFactory): AudioTrack {
        val audioSource = factory.createAudioSource(client.audioConstraints())
        return factory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
    }

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

    companion object {
        private const val VIDEO_TRACK_ID = "video-track-id"
        private const val AUDIO_TRACK_ID = "audio-track-id"
        private const val LOCAL_MEDIA_STREAM_ID = "local-media-stream"
    }
}