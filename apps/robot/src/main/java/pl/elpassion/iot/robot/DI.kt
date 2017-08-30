package pl.elpassion.iot.robot

import android.app.Application
import pl.elpassion.iot.api.Server
import pl.elpassion.iot.api.ServerImpl
import pl.elpassion.loggers.Logger
import pl.elpassion.loggers.SimpleLogger
import java.lang.UnsupportedOperationException

object DI {

    private val robot by lazy { RobotImpl(provideNewServer(), provideBabbler(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    private val babbler by lazy { Babbler(provideApplication(), provideLogger()) }

    var provideRobot: () -> Robot = { robot }

    var provideBabbler: () -> Babbler = { babbler }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> Server = { ServerImpl(9999) }
}