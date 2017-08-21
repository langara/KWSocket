package pl.elpassion.iotguard.robot

interface Robot {
    fun start(serverPort: Int)
    fun turnOff()
}