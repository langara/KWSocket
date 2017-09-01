package pl.elpassion.iot.commander

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import pl.elpassion.iot.api.*
import pl.elpassion.iot.commander.CommanderAction.*
import pl.elpassion.iot.commander.CommanderState.Connected
import pl.elpassion.iot.commander.CommanderState.Disconnected
import pl.elpassion.loggers.Logger

class CommanderImpl(private val client: Client, private val logger: Logger) : Commander {

    override val states = BehaviorRelay.createDefault<CommanderState>(Disconnected)
    override val actions = PublishRelay.create<CommanderAction>()

    init {
        client.events.subscribe { onEvent(it) }
        actions.subscribe(this::call)
    }

    private fun call(action: CommanderAction) {
        logger.log("perform($action)")
        when (action) {
            is Connect -> client.connect(action.serverAddress)
            is Disconnect -> client.disconnect()
            is Recognize -> recognize(action.speech)
            is MoveForward -> client.send("move forward")
            is MoveBackward -> client.send("move backward")
            is MoveLeft -> client.send("move left")
            is MoveRight -> client.send("move right")
            is MoveWheels -> client.send("move wheels ${action.left} ${action.right}")
            is Stop -> client.send("stop")
            is Say -> client.send("say ${action.sentence}")
            is MoveEnginesByJoystick -> client.send("joystick ${action.degrees}#${action.power}")
        }
    }

    private fun onEvent(event: Event) {
        logger.log("Commander.onEvent($event)")
        when (event) {
            is Open -> states.accept(Connected)
            is Close -> states.accept(Disconnected)
        }
    }

    private fun recognize(speech: List<String>) {
        val action = when {
            speech.matchesCommand("forward", "ahead") -> MoveForward
            speech.matchesCommand("back") -> MoveBackward
            speech.matchesCommand("left") -> MoveLeft
            speech.matchesCommand("right") -> MoveRight
            speech.matchesCommand("stop", "cancel", "enough", "abort", "wait") -> Stop
            else -> {
                val command = speech[0].toLowerCase()
                if (command.startsWith("say ")) Say(command.substring(4)) else null
            }
        }
        action?.let { actions.accept(it) } ?: logger.log("I don't understand: $speech")
    }

    private fun List<String>.matchesCommand(vararg commands: String) = any { recognized ->
        commands.any { command ->
            recognized.contains(command, ignoreCase = true)
        }
    }
}