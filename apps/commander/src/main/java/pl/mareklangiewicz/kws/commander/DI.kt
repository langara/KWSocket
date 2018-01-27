package pl.mareklangiewicz.kws.commander

import android.app.Activity
import android.app.Application
import pl.mareklangiewicz.kws.Client
import pl.mareklangiewicz.kws.WSClient
import pl.mareklangiewicz.kws.loggers.Logger
import pl.mareklangiewicz.kws.loggers.SimpleLogger

object DI {

    private val commander by lazy { CommanderImpl(provideNewWSClient(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewWSClient: () -> Client = { WSClient() }

    var provideActivity: () -> Activity = { throw UnsupportedOperationException("Activity not set") }
}
