package pl.elpassion.iotguard.robot

import io.reactivex.disposables.Disposable
import pl.elpassion.iot.api.Event
import pl.elpassion.iot.api.Message
import pl.elpassion.iot.api.Server
import pl.elpassion.loggers.Logger

class RobotImpl(private val server: Server, private val babbler: Babbler, private val logger: Logger) : Robot {

    private val motorsController = MotorController()
    private var disposable: Disposable? = null

    override fun start(serverPort: Int) {
        disposable = server.events.subscribe { onEvent(it) }
        server.start(serverPort)
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
            "stop" -> motorsController.stop()
            else ->
                if (message.startsWith("say ")) {
                    say(message.substring(4))
                } else if (message.startsWith("move wheels ")) {
                    val (left, right) = message.substring("move wheels ".length).split(" ")
                    motorsController.setupWheelsAndMove(left.toInt(), right.toInt())
                } else if (message.startsWith("joystick ")) {
                    val (degree, power) = message.substring("joystick ".length).split("#")
                    motorsController.moveEngines(degree.toInt(), power.toDouble())
                } else {
                    logger.log("TODO: handle Robot.onMessage($message)")
                }
        }
    }

    private fun say(speech: String) {
        babbler.say(speech)
    }

    override fun turnOff() {
        disposable?.dispose()
        motorsController.releasePins()
    }
}
