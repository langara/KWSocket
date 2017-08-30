package pl.elpassion.iot.alien

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.alien_activity.*
import pl.elpassion.webrtc.WebRtcManager

class AlienActivity : AppCompatActivity(), WebRtcManager.ConnectionListener {

    private var webRtcManager: WebRtcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.alien_activity)
        val username = "ALIEN"
        webRtcManager = WebRtcManager(this, surfaceView, this, username)
        webRtcManager?.startListening()
        log("Alien is ready")
    }

    override fun onDestroy() {
        webRtcManager?.cancelAllCalls()
        super.onDestroy()
    }

    override fun onConnecting(remoteUser: String) {
        log("connecting with $remoteUser")
    }

    override fun onConnected(remoteUser: String) {
        log("connected with $remoteUser")
    }

    override fun onDisconnected(remoteUser: String) {
        log("disconnected with $remoteUser")
    }

    override fun onMessage(remoteUser: String, message: String) {
        log("message: $message")
        webRtcManager?.transmit(remoteUser, "HEY") // TODO: remove it later
    }

    private fun log(message: String) {
        streamingLogView.append("$message\n")
    }
}