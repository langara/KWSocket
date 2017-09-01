package pl.elpassion.iot.commander

import android.app.Activity
import android.app.Application
import android.opengl.GLSurfaceView
import pl.elpassion.iot.api.Client
import pl.elpassion.loggers.SimpleLogger
import pl.elpassion.webrtc.WebRtcPeer
import java.util.*

object DI {

    private val commander by lazy { CommanderImpl(provideNewWebRTCClient(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> pl.elpassion.loggers.Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewWebRTCClient: () -> Client = { WebRtcPeer(provideActivity(), provideSurfaceView(), UUID.randomUUID().toString().take(5), false) }

    var provideActivity: () -> Activity = { throw UnsupportedOperationException("Activity not set") }

    var provideSurfaceView: () -> GLSurfaceView = { throw UnsupportedOperationException("Surface view not set") }
}