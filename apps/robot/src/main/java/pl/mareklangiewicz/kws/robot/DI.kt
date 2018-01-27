package pl.mareklangiewicz.kws.robot

import android.app.Application
import pl.mareklangiewicz.kws.Server
import pl.mareklangiewicz.kws.WSServer
import pl.mareklangiewicz.kws.loggers.Logger
import pl.mareklangiewicz.kws.loggers.SimpleLogger
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
