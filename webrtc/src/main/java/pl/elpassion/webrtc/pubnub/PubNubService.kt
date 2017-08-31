package pl.elpassion.webrtc.pubnub

import com.pubnub.api.Callback
import com.pubnub.api.Pubnub
import org.json.JSONObject
import pl.elpassion.webrtc.BuildConfig
import pl.elpassion.webrtc.WebRtcService

class PubNubService : WebRtcService {

    private val pubNub by lazy { Pubnub(BuildConfig.PN_PUB_KEY, BuildConfig.PN_SUB_KEY) }

    override fun startListening(localUser: String, onCall: (String) -> Unit) {
        pubNub.uuid = localUser
        pubNub.subscribe(localUser.channel, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                if (message !is JSONObject) return
                if (message.has(CALL_USER)) {
                    val remoteUser = message.getString(CALL_USER)
                    onCall(remoteUser)
                }
            }
        })
    }

    override fun callUser(localUser: String, remoteUser: String, onCall: (String) -> Unit) {
        val message = JSONObject().apply { put(CALL_USER, localUser) }
        pubNub.publish(remoteUser.channel, message, object : Callback() {
            override fun successCallback(channel: String, message: Any) {
                onCall(remoteUser)
            }
        })
    }

    private val String.channel get() = this + "-channel"

    companion object {
        private const val CALL_USER = "call_user"
    }
}