package pl.elpassion.iot.commander

sealed class CommanderState {
    object Disconnected : CommanderState()
    object Connected : CommanderState()
}

