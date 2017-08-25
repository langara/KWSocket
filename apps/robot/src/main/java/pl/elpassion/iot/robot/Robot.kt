package pl.elpassion.iot.robot

interface Robot {
    fun start(serverPort: Int)
    fun turnOff()
}