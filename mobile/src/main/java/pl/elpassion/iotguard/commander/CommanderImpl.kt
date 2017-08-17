package pl.elpassion.iotguard.commander

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import pl.elpassion.iotguard.Logger
import pl.elpassion.iotguard.api.*
import pl.elpassion.iotguard.commander.CommanderAction.*
import pl.elpassion.iotguard.commander.CommanderState.Connected
import pl.elpassion.iotguard.commander.CommanderState.Disconnected

class CommanderImpl(private val client: Client, private val logger: Logger) : Commander {

    init {
        client.events.subscribe { onEvent(it) }
    }

    private val statesSubject = BehaviorSubject.createDefault<CommanderState>(Disconnected)

    override val states: Observable<CommanderState> = statesSubject.hide()

    override fun perform(action: CommanderAction) {
        logger.log("perform($action)")
        when (action) {
            is Connect -> connect(action.robotAddress)
            is Recognize -> recognize(action.speech)
            is MoveForward -> client.send("move forward")
            is MoveBackward -> client.send("move backward")
            is MoveLeft -> client.send("move left")
            is MoveRight -> client.send("move right")
            is Stop -> client.send("stop")
            is Say -> client.send("say ${action.sentence}")
        }
    }

    private fun connect(robotAddress: String) = client.connect(robotAddress)

    private fun onEvent(event: Event) {
        when (event) {
            is Open -> statesSubject.onNext(Connected)
            is Close -> statesSubject.onNext(Disconnected)
            else -> logger.log("TODO: Commander.onEvent($event)")
        }
    }

    private fun recognize(speech: String) {
        val action = when(speech) {
            "move forward" -> MoveForward
            "move backward" -> MoveBackward
            "move left" -> MoveLeft
            "move right" -> MoveRight
            "stop" -> Stop
            else -> if (speech.startsWith("say ")) Say(speech.substring(4)) else null
        }
        action?.let { perform(it) } ?: logger.log("I don't understand: $speech")
    }
}