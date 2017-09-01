package pl.elpassion.iot.alien

import android.app.Activity
import android.app.Application
import android.opengl.GLSurfaceView
import pl.elpassion.iot.api.Client
import pl.elpassion.iot.api.Server
import pl.elpassion.iot.api.WSClient
import pl.elpassion.loggers.Logger
import pl.elpassion.loggers.SimpleLogger
import pl.elpassion.webrtc.WebRtcPeer
import java.lang.UnsupportedOperationException

object DI {

    private val alien by lazy { pl.elpassion.iot.alien.AlienImpl(provideNewWebRTCServer(), provideNewWSClient(), provideBabbler(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    private val babbler by lazy { pl.elpassion.iot.alien.Babbler(provideApplication(), provideLogger()) }

    var provideAlien: () -> pl.elpassion.iot.alien.Alien = { alien }

    var provideBabbler: () -> pl.elpassion.iot.alien.Babbler = { babbler }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideActivity: () -> Activity = { throw UnsupportedOperationException("Activity not set") }

    var provideSurfaceView: () -> GLSurfaceView = { throw UnsupportedOperationException("Surface view not set") }

    var provideNewWebRTCServer: () -> Server = { WebRtcPeer(provideActivity(), provideSurfaceView(), "ALIEN") }

    var provideNewWSClient: () -> Client = { WSClient() }
}