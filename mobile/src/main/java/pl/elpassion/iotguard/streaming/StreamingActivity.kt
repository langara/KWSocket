package pl.elpassion.iotguard.streaming

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.streaming_activity.*
import net.majorkernelpanic.streaming.Session
import net.majorkernelpanic.streaming.SessionBuilder
import net.majorkernelpanic.streaming.audio.AudioQuality
import net.majorkernelpanic.streaming.rtsp.RtspServer
import net.majorkernelpanic.streaming.video.VideoQuality
import pl.elpassion.iotguard.AndroidLogger
import pl.elpassion.iotguard.R
import java.lang.Exception

class StreamingActivity : AppCompatActivity(), Session.Callback, SurfaceHolder.Callback {

    private val logger = AndroidLogger("IoT Streaming")
    private var session: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.streaming_activity)
        startButton.setOnClickListener {
            session?.configure()
            startButton.isEnabled = false
        }
        session = createSession()
        surfaceView.holder.addCallback(this)
        startService(Intent(this, RtspServer::class.java))
        logWiFiDetails()
    }

    override fun onDestroy() {
        stopService(Intent(this, RtspServer::class.java))
        super.onDestroy()
    }

    private fun createSession() = SessionBuilder.getInstance()
            .setCallback(this)
            .setSurfaceView(surfaceView)
            .setContext(applicationContext)
            .setAudioEncoder(SessionBuilder.AUDIO_NONE)
            .setAudioQuality(AudioQuality(16000, 32000))
            .setVideoEncoder(SessionBuilder.VIDEO_H264)
            .setVideoQuality(VideoQuality(320, 240, 20, 500000))
            .build()

    override fun onPreviewStarted() {
        logger.log("onPreviewStarted")
    }

    override fun onSessionConfigured() {
        logger.log("onSessionConfigured")
        session?.start()
    }

    override fun onSessionStarted() {
        logger.log("onSessionStarted")
    }

    override fun onSessionStopped() {
        logger.log("onSessionStopped")
    }

    override fun onBitrateUpdate(bitrate: Long) {
        logger.log("onBitrateUpdate: $bitrate")
    }

    override fun onSessionError(reason: Int, streamType: Int, e: Exception?) {
        logger.log("onSessionError: $reason, $streamType, $e")
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        logger.log("surfaceCreated: $holder")
        session?.startPreview()
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        logger.log("surfaceChanged: $holder, $format, $width, $height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        logger.log("surfaceDestroyed: $holder")
        session?.stop()
    }

    private fun logWiFiDetails() {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        val addr = Formatter.formatIpAddress(info.ipAddress)
        logger.log("Wifi info: $info")
        logger.log("Wifi ip address: $addr")
    }
}