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
        when (action) {
            is MoveForward -> client.send("move forward")
            is MoveBackward -> client.send("move backward")
            is MoveLeft -> client.send("move left")
            is MoveRight -> client.send("move right")
            is Stop -> client.send("stop")
            is Connect -> connect(action.robotAddress)
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
}