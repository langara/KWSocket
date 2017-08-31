package pl.elpassion.webrtc

interface WebRtcStreaming {
    fun startListening(onCall: (String) -> Unit)
    fun callUser(remoteUser: String, onCall: (String) -> Unit)
}