package pl.elpassion.webrtc

interface WebRtcService {
    fun startListening(localUser: String, onCall: (String) -> Unit)
    fun callUser(localUser: String, remoteUser: String, onCall: (String) -> Unit)
}