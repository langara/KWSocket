package pl.elpassion.iotguard.robot

sealed class RobotState {
    object Enabled : RobotState()
    object Disabled : RobotState()
}