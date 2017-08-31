package pl.elpassion.iot.alien

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.alien_activity.*
import pl.elpassion.iot.api.Event
import pl.elpassion.iot.api.Message
import pl.elpassion.webrtc.WebRtcPeer

class AlienActivity : AppCompatActivity() {

    private lateinit var webRtcPeer: WebRtcPeer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.alien_activity)
        val username = "ALIEN"
        webRtcPeer = WebRtcPeer(this, surfaceView, username)
        webRtcPeer.events.subscribe(this::onEvent)
        webRtcPeer.start()
        log("Alien is ready")
    }

    override fun onDestroy() {
        webRtcPeer.close()
        super.onDestroy()
    }

    private fun onEvent(event: Event) {
        log("AlienActivity.onEvent($event)")
        if (event is Message)
            event.source?.send("Hey!") // TODO: remove it later
    }

    private fun log(message: String) {
        streamingLogView.post {
            streamingLogView.append("$message\n")
        }
    }
}