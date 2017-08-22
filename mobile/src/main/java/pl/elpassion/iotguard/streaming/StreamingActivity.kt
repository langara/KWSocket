@file:Suppress("INACCESSIBLE_TYPE")

package pl.elpassion.iotguard.streaming

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pubnub.api.Callback
import com.pubnub.api.Pubnub
import kotlinx.android.synthetic.main.streaming_activity.*
import me.kevingleason.pnwebrtc.PnRTCClient
import org.json.JSONObject
import org.webrtc.*
import pl.elpassion.iotguard.BuildConfig
import pl.elpassion.iotguard.R
import java.util.*


class StreamingActivity : AppCompatActivity() {

    private val localUser = UUID.randomUUID().toString().take(5)
    private var pubNub: Pubnub? = null
    private var rtcClient: PnRTCClient? = null
    private var localVideoSource: VideoSource? = null
    private var localRender: VideoRenderer.Callbacks? = null
    private var remoteRender: VideoRenderer.Callbacks? = null
    private var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.streaming_activity)
        initWebRtc()
        initSignalingService()
        localUserView.text = localUser
        connectButton.setOnClickListener { makeCall() }
    }

    private fun initWebRtc() {
        PeerConnectionFactory.initializeAndroidGlobals(
                this, // Context
                true, // Audio Enabled
                true, // Video Enabled
                true, // Hardware Acceleration Enabled
                null) // Render EGL Context

        rtcClient = PnRTCClient(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY, localUser)

        VideoRendererGui.setView(surfaceView, null)
        localRender = createVideoRenderer()
        remoteRender = createVideoRenderer()

        val factory = PeerConnectionFactory()

        val mediaStream = factory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID)
        createVideoTrack(factory)?.let { mediaStream.addTrack(it) }
        mediaStream.addTrack(createAudioTrack(factory))

        val rtcListener = RtcListener(this, localRender, remoteRender)
        rtcClient?.run {
            attachRTCListener(rtcListener)
            attachLocalMediaStream(mediaStream)
            listenOn(localUser)
            setMaxConnections(1)
        }
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

    private fun initSignalingService() {
        val uuid = localUser.channel
        pubNub = Pubnub(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY)
        pubNub?.uuid = localUser
        pubNub?.subscribe(uuid, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                if (message !is JSONObject) return
                if (message.has(CALL_USER)) {
                    user = message.getString(CALL_USER)
                    rtcClient?.connect(user, false)
                }
            }
        })
    }

    private fun makeCall() {
        val remoteUser = remoteUserEditText.text.toString()
        val message = JSONObject()
        message.put(CALL_USER, localUser)
        pubNub?.publish(remoteUser.channel, message, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                rtcClient?.connect(remoteUser, true)
            }
        })
    }

    private val String.channel get() = this + "-channel"

    companion object {
        private const val VIDEO_TRACK_ID = "video-track-id"
        private const val AUDIO_TRACK_ID = "audio-track-id"
        private const val LOCAL_MEDIA_STREAM_ID = "local-media-stream"
        private const val CALL_USER = "call_user"
    }
}