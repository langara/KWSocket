@file:Suppress("INACCESSIBLE_TYPE")

package pl.elpassion.iotguard.streaming

import android.app.Activity
import android.opengl.GLSurfaceView
import com.pubnub.api.Callback
import com.pubnub.api.Pubnub
import me.kevingleason.pnwebrtc.PnPeer
import me.kevingleason.pnwebrtc.PnRTCClient
import me.kevingleason.pnwebrtc.PnRTCListener
import org.json.JSONObject
import org.webrtc.*
import pl.elpassion.iotguard.BuildConfig

class WebRtcManager(private val activity: Activity,
                    surfaceView: GLSurfaceView,
                    private val listener: ConnectionListener,
                    private val username: String) {

    interface ConnectionListener {
        fun onConnecting(remoteUser: String)
        fun onConnected(remoteUser: String)
        fun onDisconnected(remoteUser: String)
    }

    private var pubNub: Pubnub? = null
    private var rtcClient: PnRTCClient? = null
    private var localVideoSource: VideoSource? = null
    private var localRender: VideoRenderer.Callbacks? = null
    private var remoteRender: VideoRenderer.Callbacks? = null

    init {
        PeerConnectionFactory.initializeAndroidGlobals(
                activity, // Context
                true, // Audio Enabled
                true, // Video Enabled
                true, // Hardware Acceleration Enabled
                null) // Render EGL Context

        rtcClient = PnRTCClient(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY, username)

        VideoRendererGui.setView(surfaceView, null)
        localRender = createVideoRenderer()
        remoteRender = createVideoRenderer()

        val factory = PeerConnectionFactory()

        val mediaStream = factory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID)
        createVideoTrack(factory)?.let { mediaStream.addTrack(it) }
        mediaStream.addTrack(createAudioTrack(factory))

        rtcClient?.run {
            attachRTCListener(RtcListener())
            attachLocalMediaStream(mediaStream)
            listenOn(username)
            setMaxConnections(5)
        }
    }

    fun startListening() {
        val uuid = username.channel
        pubNub = Pubnub(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY)
        pubNub?.uuid = username
        pubNub?.subscribe(uuid, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                if (message !is JSONObject) return
                if (message.has(CALL_USER)) {
                    val remoteUsername = message.getString(CALL_USER)
                    rtcClient?.connect(remoteUsername, false)
                }
            }
        })
    }

    fun callUser(remoteUsername: String) {
        val message = JSONObject()
        message.put(CALL_USER, username)
        pubNub?.publish(remoteUsername.channel, message, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                rtcClient?.connect(remoteUsername, true)
            }
        })
    }

    fun cancelCall() {
        rtcClient?.closeAllConnections()
    }

    private fun createVideoRenderer() = VideoRendererGui.create(0, 0, 100, 100,
            VideoRendererGui.ScalingType.SCALE_ASPECT_FILL, false)

    private fun createVideoTrack(factory: PeerConnectionFactory): VideoTrack? {
        val cameraDevice = VideoCapturerAndroid.getNameOfBackFacingDevice()
        val capturer = VideoCapturerAndroid.create(cameraDevice)
        return if (capturer != null) {
            localVideoSource = factory.createVideoSource(capturer, rtcClient?.videoConstraints())
            factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        } else null
    }

    private fun createAudioTrack(factory: PeerConnectionFactory): AudioTrack {
        val audioSource = factory.createAudioSource(rtcClient?.audioConstraints())
        return factory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
    }

    private val String.channel get() = this + "-channel"

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

        override fun onMessage(peer: PnPeer?, message: Any?) {
            super.onMessage(peer, message)
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
        private const val CALL_USER = "call_user"
    }
}