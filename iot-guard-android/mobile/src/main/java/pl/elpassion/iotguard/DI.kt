package pl.elpassion.iotguard

import android.app.Application
import pl.elpassion.iotguard.api.Client
import pl.elpassion.iotguard.api.ClientImpl
import pl.elpassion.iotguard.api.Server
import pl.elpassion.iotguard.api.ServerImpl
import pl.elpassion.iotguard.commander.Commander
import pl.elpassion.iotguard.commander.CommanderImpl

object DI {

    private val commander by lazy { CommanderImpl() }

    private val simpleLogger by lazy { SimpleLogger() }

    var provideCommander: () -> Commander = { commander }

    var provideLogger: () -> Logger = { simpleLogger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewServer: (port: Int) -> Server = { ServerImpl(it) }

    var provideNewClient: (serverURI: String) -> Client = { ClientImpl(it) }
}