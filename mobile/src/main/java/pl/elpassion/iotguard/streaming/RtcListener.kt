package pl.elpassion.iotguard.streaming

import android.app.Activity
import me.kevingleason.pnwebrtc.PnPeer
import me.kevingleason.pnwebrtc.PnRTCListener
import org.webrtc.MediaStream
import org.webrtc.VideoRenderer
import org.webrtc.VideoRendererGui

class RtcListener(private val activity: Activity,
                  private val localRenderer: VideoRenderer.Callbacks?,
                  private val remoteRenderer: VideoRenderer.Callbacks?) : PnRTCListener() {

    override fun onLocalStream(localStream: MediaStream) {
        super.onLocalStream(localStream)
        activity.runOnUiThread {
            if (localStream.videoTracks.isNotEmpty()) {
                localStream.videoTracks[0].addRenderer(VideoRenderer(localRenderer))
            }
        }
    }

    override fun onAddRemoteStream(remoteStream: MediaStream, peer: PnPeer) {
        super.onAddRemoteStream(remoteStream, peer)
        activity.runOnUiThread {
            if (remoteStream.videoTracks.isNotEmpty()) {
                remoteStream.videoTracks[0].addRenderer(VideoRenderer(remoteRenderer))
            }
            updateRenderer(remoteRenderer, 0, 0, 100, 100, false)
            updateRenderer(localRenderer, 72, 72, 25, 25, true)
        }
    }

    private fun updateRenderer(renderer: VideoRenderer.Callbacks?,
                               x: Int, y: Int, width: Int, height: Int, mirror: Boolean) {
        val scalingType = VideoRendererGui.ScalingType.SCALE_ASPECT_FILL
        VideoRendererGui.update(renderer, x, y, width, height, scalingType, mirror)
    }

    override fun onMessage(peer: PnPeer?, message: Any?) {
        super.onMessage(peer, message)
    }

    override fun onPeerConnectionClosed(peer: PnPeer?) {
        super.onPeerConnectionClosed(peer)
    }
}