package pl.elpassion.iotguard

import android.app.Application
import pl.elpassion.iot.api.Client
import pl.elpassion.iot.api.WSClient
import pl.elpassion.iot.api.Server
import pl.elpassion.iot.api.WSServer
import pl.elpassion.loggers.Logger
import pl.elpassion.loggers.SimpleLogger
import java.lang.UnsupportedOperationException

object DI {

    private val logger by lazy { SimpleLogger() }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> Server = { WSServer(9999) }

    var provideNewClient: () -> Client = { WSClient() }
}