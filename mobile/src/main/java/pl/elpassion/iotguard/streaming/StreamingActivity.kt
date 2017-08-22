package pl.elpassion.iotguard.streaming

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.streaming_activity.*
import pl.elpassion.iotguard.R
import java.util.*

class StreamingActivity : AppCompatActivity() {

    private var webRtcManager: WebRtcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.streaming_activity)
        val username = createRandomUsername()
        webRtcManager = WebRtcManager(this, surfaceView, username)
        webRtcManager?.startListening()
        localUserView.text = username
        connectButton.setOnClickListener { callUser() }
    }

    private fun callUser() {
        val remoteUsername = remoteUserEditText.text.toString()
        webRtcManager?.callUser(remoteUsername)
    }

    private fun createRandomUsername() = UUID.randomUUID().toString().take(5)
}