package pl.elpassion.iotguard

import android.app.Application

object DI {

    private val commanderModel by lazy { CommanderModelImpl() }

    private val simpleLogger by lazy { SimpleLogger() }

    var provideCommanderModel: () -> CommanderModel = { commanderModel }

    var provideLogger: () -> Logger = { simpleLogger }

    var provideApplication: () -> Application = { throw UnsupportedOperationException("Application provider not initialized") }
}