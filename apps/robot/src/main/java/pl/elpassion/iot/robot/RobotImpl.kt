package pl.elpassion.iot.robot

import io.reactivex.disposables.Disposable
import pl.elpassion.iot.api.Event
import pl.elpassion.iot.api.Message
import pl.elpassion.iot.api.Server
import pl.elpassion.loggers.Logger

class RobotImpl(private val server: Server, private val logger: Logger) : Robot {

    private val motorsController = MotorController()
    private var disposable: Disposable? = null

    override fun start() {
        disposable = server.events.subscribe { onEvent(it) }
        server.start()
    }

    private fun onEvent(event: Event) {
        logger.log("onEvent($event)")
        when (event) {
            is Message -> onMessage(event.message)
            else -> logger.log("TODO: handle Robot.onEvent($event)")
        }
    }

    private fun onMessage(message: String) {
        when (message) {
            "move forward" -> motorsController.moveForward()
            "move backward" -> motorsController.moveBackward()
            "move left" -> motorsController.moveLeft()
            "move right" -> motorsController.moveRight()
            "look up" -> motorsController.lookUp()
            "look down" -> motorsController.lookDown()
            "look ahead" -> motorsController.lookAhead()
            "stop" -> motorsController.stop()
            else -> when {
                message.startsWith("say ") -> logger.log("This robot does not speak")
                message.startsWith("move wheels ") -> moveWheels(message)
                else -> logger.log("TODO: handle Robot.onMessage($message)")
            }
        }
    }

    private fun moveWheels(message: String) {
        val (left, right) = message.substring("move wheels ".length).split(" ")
        motorsController.setupWheelsAndMove(left.toInt(), right.toInt())
    }

    override fun turnOff() {
        server.disconnect()
        disposable?.dispose()
        motorsController.releasePins()
    }
}
