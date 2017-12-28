package pl.elpassion.iot.robot

import android.app.Application
import pl.elpassion.iot.api.Server
import pl.elpassion.iot.api.WSServer
import pl.elpassion.loggers.Logger
import pl.elpassion.loggers.SimpleLogger
import java.lang.UnsupportedOperationException

object DI {

    private val robot by lazy { RobotImpl(provideNewWSServer(), provideBabbler(), provideApplication(), provideLogger()) }

    private val babbler by lazy { Babbler(provideApplication(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    var provideRobot: () -> Robot = { robot }

    var provideBabbler: () -> Babbler = { babbler }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewWSServer: () -> Server = { WSServer(9999) }
}