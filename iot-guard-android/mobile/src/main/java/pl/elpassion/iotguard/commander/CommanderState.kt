package pl.elpassion.iotguard.commander

sealed class CommanderState {
    object Disconnected : CommanderState()
    object Connected : CommanderState()
}

