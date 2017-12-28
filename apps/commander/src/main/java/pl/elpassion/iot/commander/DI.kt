package pl.elpassion.iot.commander

import android.app.Activity
import android.app.Application
import pl.elpassion.iot.api.Client
import pl.elpassion.iot.api.WSClient
import pl.elpassion.loggers.SimpleLogger

object DI {

    private val commander by lazy { CommanderImpl(provideNewWSClient(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> pl.elpassion.loggers.Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewWSClient: () -> Client = { WSClient() }

    var provideActivity: () -> Activity = { throw UnsupportedOperationException("Activity not set") }
}