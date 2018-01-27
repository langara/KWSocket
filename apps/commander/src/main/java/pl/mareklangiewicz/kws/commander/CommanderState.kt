package pl.mareklangiewicz.kws.commander

sealed class CommanderState {
    object Disconnected : CommanderState()
    object Connected : CommanderState()
}

