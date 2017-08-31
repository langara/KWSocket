package pl.elpassion.webrtc.pubnub

import com.pubnub.api.Callback
import com.pubnub.api.Pubnub
import org.json.JSONObject
import pl.elpassion.webrtc.BuildConfig
import pl.elpassion.webrtc.WebRtcService

class PubNubService : WebRtcService {

    private val pubNub by lazy { Pubnub(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY) }

    override fun startListening(localUsername: String, onCall: (String) -> Unit) {
        pubNub.uuid = localUsername
        pubNub.subscribe(localUsername.channel, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                if (message !is JSONObject) return
                if (message.has(CALL_USER)) {
                    val remoteUsername = message.getString(CALL_USER)
                    onCall(remoteUsername)
                }
            }
        })
    }

    override fun callUser(localUsername: String, remoteUsername: String, onCall: (String) -> Unit) {
        val message = JSONObject().apply { put(CALL_USER, localUsername) }
        pubNub.publish(remoteUsername.channel, message, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                onCall(remoteUsername)
            }
        })
    }

    private val String.channel get() = this + "-channel"

    companion object {
        private const val CALL_USER = "call_user"
    }
}