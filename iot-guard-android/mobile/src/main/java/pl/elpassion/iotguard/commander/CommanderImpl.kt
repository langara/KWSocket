package pl.elpassion.iotguard.commander

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.api.Client
import pl.elpassion.iotguard.api.Close
import pl.elpassion.iotguard.api.Event
import pl.elpassion.iotguard.api.Open

class CommanderImpl : Commander {

    private val logger by lazy { DI.provideLogger() }

    private val statesSubject = BehaviorSubject.createDefault<CommanderState>(Disconnected)

    override val states: Observable<CommanderState> = statesSubject.hide()

    private var client: Client? = null

    override fun perform(action: CommanderAction) {
        when(action) {
            is MoveForward -> client?.send("move forward")
            is MoveBackward -> client?.send("move backward")
            is MoveLeft -> client?.send("move left")
            is MoveRight -> client?.send("move right")
            is Stop -> client?.send("stop")
            is Connect -> connect(action.robotAddress)
        }
    }

    private fun connect(robotAddress: String) {
        client?.close()
        statesSubject.onNext(Disconnected)
        client = DI.provideNewClient(robotAddress).apply {
            events.subscribe { onEvent(it) }
            connect()
        }

    }

    private fun onEvent(event: Event) {
        when (event) {
            is Open -> statesSubject.onNext(Connected)
            is Close -> statesSubject.onNext(Disconnected)
            else -> logger.log("TODO: Commander.onEvent($event)")
        }
    }
}