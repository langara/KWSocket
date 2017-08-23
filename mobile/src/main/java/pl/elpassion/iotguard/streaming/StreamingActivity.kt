package pl.elpassion.iotguard.streaming

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.streaming_activity.*
import pl.elpassion.iotguard.R
import java.util.*

class StreamingActivity : AppCompatActivity(), WebRtcManager.ConnectionListener {

    private var webRtcManager: WebRtcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.streaming_activity)
        val username = createRandomUsername()
        webRtcManager = WebRtcManager(this, surfaceView, this, username)
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
        webRtcManager?.cancelCall()
    }

    private fun createRandomUsername() = UUID.randomUUID().toString().take(5)
}