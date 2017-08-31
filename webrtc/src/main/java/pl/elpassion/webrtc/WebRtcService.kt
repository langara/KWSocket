package pl.elpassion.webrtc

interface WebRtcService {
    fun startListening(localUsername: String, onCall: (String) -> Unit)
    fun callUser(localUsername: String, remoteUsername: String, onCall: (String) -> Unit)
}