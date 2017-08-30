package pl.elpassion.iot.commander

import android.app.Application
import pl.elpassion.iot.api.Client
import pl.elpassion.iot.api.ClientImpl
import pl.elpassion.iot.api.Server
import pl.elpassion.iot.api.ServerImpl
import pl.elpassion.loggers.SimpleLogger

object DI {

    private val commander by lazy { CommanderImpl(provideNewClient(), provideLogger()) }

    private val logger by lazy { SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> pl.elpassion.loggers.Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> Server = { ServerImpl(9999) }

    var provideNewClient: () -> Client = { ClientImpl() }
}