package pl.elpassion.iotguard.robot

sealed class RobotAction {
    data class Start(val serverPort: Int = 80) : RobotAction()
}