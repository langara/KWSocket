package pl.elpassion.iot.commander

import android.app.Application
import pl.elpassion.iot.api.ClientImpl
import pl.elpassion.iot.api.ServerImpl

object DI {

    private val commander by lazy { CommanderImpl(provideNewClient(), provideLogger()) }

    private val logger by lazy { pl.elpassion.loggers.SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> pl.elpassion.loggers.Logger = { logger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: () -> pl.elpassion.iot.api.Server = { ServerImpl() }

    var provideNewClient: () -> pl.elpassion.iot.api.Client = { ClientImpl() }
}