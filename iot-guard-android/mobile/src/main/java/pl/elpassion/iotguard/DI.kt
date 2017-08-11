package pl.elpassion.iotguard

import android.app.Application
import pl.elpassion.iotguard.api.Client
import pl.elpassion.iotguard.api.ClientImpl

object DI {

    private val commanderModel by lazy { CommanderModelImpl() }

    private val simpleLogger by lazy { SimpleLogger() }

    var provideCommanderModel: () -> CommanderModel = { commanderModel }

    var provideLogger: () -> Logger = { simpleLogger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }

    var provideNewClient: (serverURI: String) -> Client = { ClientImpl(it) }
}