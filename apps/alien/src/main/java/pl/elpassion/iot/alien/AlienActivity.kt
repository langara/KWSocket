package pl.elpassion.iot.alien

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.alien_activity.*

class AlienActivity : AppCompatActivity(), pl.elpassion.webrtc.WebRtcManager.ConnectionListener {

    private var webRtcManager: pl.elpassion.webrtc.WebRtcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.alien_activity)
        val username = "ALIEN"
        webRtcManager = pl.elpassion.webrtc.WebRtcManager(this, surfaceView, this, username)
        webRtcManager?.startListening()
        localUserView.text = username
        connectButton.setOnClickListener { callUser() }
        disconnectButton.setOnClickListener { cancelCall() }
    }

    override fun onConnecting(remoteUser: String) {
        connectButton.hide()
        progressBar.show()
        streamingLogView.append("connecting with $remoteUser\n")
    }

    override fun onConnected(remoteUser: String) {
        localUserView.hide()
        remoteUserEditText.hide()
        progressBar.hide()
        disconnectButton.show()
        streamingLogView.append("connected with $remoteUser\n")
    }

    override fun onDisconnected(remoteUser: String) {
        disconnectButton.hide()
        connectButton.show()
        localUserView.show()
        remoteUserEditText.show()
        streamingLogView.append("disconnected with $remoteUser\n")
    }

    private fun callUser() {
        val remoteUsername = remoteUserEditText.text.toString()
        webRtcManager?.callUser(remoteUsername)
    }

    private fun cancelCall() {
        webRtcManager?.cancelAllCalls()
    }

}