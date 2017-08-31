package pl.elpassion.webrtc

interface WebRtcService {
    fun startListening(onCall: (String) -> Unit)
    fun callUser(remoteUser: String, onCall: (String) -> Unit)
}