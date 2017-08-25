package pl.elpassion.iot.commander

sealed class CommanderAction {
    object MoveForward : CommanderAction()
    object MoveBackward : CommanderAction()
    object MoveLeft : CommanderAction()
    object MoveRight : CommanderAction()
    object Stop : CommanderAction()

    data class MoveWheels(val left: Int, val right: Int) : CommanderAction()
    data class MoveEnginesByJoystick(val degrees: Int, val power: Double) : CommanderAction()
    data class Connect(val robotAddress: String) : CommanderAction()
    data class Say(val sentence: String) : CommanderAction()
    data class Recognize(val speech: String) : CommanderAction()
}


