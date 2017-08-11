package pl.elpassion.iotguard


object DI {


    private val commanderModel by lazy { CommanderModelImpl() }

    var provideCommanderModel: () -> CommanderModel = { commanderModel }
}