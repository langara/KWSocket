@file:Suppress("INACCESSIBLE_TYPE")

package pl.elpassion.iotguard.streaming

import android.app.Activity
import android.opengl.GLSurfaceView
import com.pubnub.api.Callback
import com.pubnub.api.Pubnub
import me.kevingleason.pnwebrtc.PnRTCClient
import org.json.JSONObject
import org.webrtc.*
import pl.elpassion.iotguard.BuildConfig

class WebRtcManager(activity: Activity, surfaceView: GLSurfaceView, private val username: String) {

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

        val rtcListener = RtcListener(activity, localRender, remoteRender)
        rtcClient?.run {
            attachRTCListener(rtcListener)
            attachLocalMediaStream(mediaStream)
            listenOn(username)
            setMaxConnections(1)
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

    companion object {
        private const val VIDEO_TRACK_ID = "video-track-id"
        private const val AUDIO_TRACK_ID = "audio-track-id"
        private const val LOCAL_MEDIA_STREAM_ID = "local-media-stream"
        private const val CALL_USER = "call_user"
    }
}