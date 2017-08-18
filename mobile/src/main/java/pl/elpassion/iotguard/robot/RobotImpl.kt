package pl.elpassion.iotguard.robot

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import pl.elpassion.iotguard.Logger
import pl.elpassion.iotguard.api.*
import pl.elpassion.iotguard.robot.RobotState.Disabled

class RobotImpl(private val server: Server, private val babbler: Babbler, private val logger: Logger) : Robot {

    init {
        server.events.subscribe { onEvent(it) }
    }

    private val statesSubject = BehaviorSubject.createDefault<RobotState>(Disabled)

    override val states: Observable<RobotState> = statesSubject.hide()

    override fun perform(action: RobotAction) {
        logger.log("perform($action)")
        when(action) {
            is RobotAction.Start -> start(action.serverPort)
        }
    }

    private fun start(serverPort: Int) {
        server.start(serverPort)
    }

    private fun onEvent(event: Event) {
        when (event) {
            is Open -> statesSubject.onNext(RobotState.Enabled)
            is Close -> statesSubject.onNext(RobotState.Disabled)
            is Message -> onMessage(event.message)
            else -> logger.log("TODO: Robot.onEvent($event)")
        }
    }

    private fun onMessage(message: String) {
        if (message.startsWith("say "))
            say(message.substring(4))
        else
            logger.log("TODO: Robot.onMessage($message)")
    }

    private fun say(speech: String) {
        babbler.say(speech)
    }
}