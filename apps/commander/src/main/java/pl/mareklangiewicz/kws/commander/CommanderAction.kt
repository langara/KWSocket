package pl.mareklangiewicz.kws.commander

sealed class CommanderAction {
    object MoveForward : CommanderAction()
    object MoveBackward : CommanderAction()
    object MoveLeft : CommanderAction()
    object MoveRight : CommanderAction()
    object Stop : CommanderAction()

    data class MoveWheels(val left: Int, val right: Int) : CommanderAction()
    data class Connect(val serverAddress: String) : CommanderAction()
    object Disconnect : CommanderAction()
    data class Say(val sentence: String) : CommanderAction()
    data class Recognize(val speech: List<String>) : CommanderAction()

    object LookUp : CommanderAction()
    object LookDown : CommanderAction()
    object LookAhead : CommanderAction()

    data class ChangeVolume(val delta: Int) : CommanderAction()
}


