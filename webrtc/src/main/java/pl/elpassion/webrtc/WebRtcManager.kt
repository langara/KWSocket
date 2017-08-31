@file:Suppress("INACCESSIBLE_TYPE")

package pl.elpassion.webrtc

import android.app.Activity
import android.opengl.GLSurfaceView
import org.webrtc.*
import pl.elpassion.webrtc.pubnub.PubNubClient
import pl.elpassion.webrtc.pubnub.PubNubStreaming

class WebRtcManager(private val activity: Activity,
                    surfaceView: GLSurfaceView,
                    private val listener: WebRtcClient.ConnectionListener,
                    private val username: String) {

    val client: WebRtcClient by lazy { PubNubClient(activity, username, listener) }
    val streaming: WebRtcStreaming by lazy { PubNubStreaming(username) }

    init {
        PeerConnectionFactory.initializeAndroidGlobals(
                activity, // Context
                true, // Audio Enabled
                true, // Video Enabled
                true, // Hardware Acceleration Enabled
                null) // Render EGL Context

        VideoRendererGui.setView(surfaceView, null)
        client.localRender = createVideoRender()
        client.remoteRender = createVideoRender()

        client.init(createMediaStream())
    }

    private fun createVideoRender() = VideoRendererGui.create(0, 0, 100, 100,
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
            val localVideoSource = factory.createVideoSource(capturer, client.getVideoConstraints())
            factory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource)
        } else null
    }

    private fun createAudioTrack(factory: PeerConnectionFactory): AudioTrack {
        val audioSource = factory.createAudioSource(client.getAudioConstraints())
        return factory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
    }

    companion object {
        private const val VIDEO_TRACK_ID = "video-track-id"
        private const val AUDIO_TRACK_ID = "audio-track-id"
        private const val LOCAL_MEDIA_STREAM_ID = "local-media-stream"
    }
}