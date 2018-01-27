package pl.mareklangiewicz.kws.commander

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import pl.mareklangiewicz.kws.Client
import pl.mareklangiewicz.kws.Close
import pl.mareklangiewicz.kws.Event
import pl.mareklangiewicz.kws.Open
import pl.mareklangiewicz.kws.commander.CommanderAction.ChangeVolume
import pl.mareklangiewicz.kws.commander.CommanderAction.Connect
import pl.mareklangiewicz.kws.commander.CommanderAction.Disconnect
import pl.mareklangiewicz.kws.commander.CommanderAction.LookAhead
import pl.mareklangiewicz.kws.commander.CommanderAction.LookDown
import pl.mareklangiewicz.kws.commander.CommanderAction.LookUp
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveBackward
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveForward
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveLeft
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveRight
import pl.mareklangiewicz.kws.commander.CommanderAction.MoveWheels
import pl.mareklangiewicz.kws.commander.CommanderAction.Recognize
import pl.mareklangiewicz.kws.commander.CommanderAction.Say
import pl.mareklangiewicz.kws.commander.CommanderAction.Stop
import pl.mareklangiewicz.kws.commander.CommanderState.Connected
import pl.mareklangiewicz.kws.commander.CommanderState.Disconnected
import pl.mareklangiewicz.kws.loggers.Logger
import pl.mareklangiewicz.kws.send

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
            is LookUp -> client.send("look up")
            is LookDown -> client.send("look down")
            is LookAhead -> client.send("look ahead")
            is ChangeVolume -> client.send("volume ${action.delta}")
        }
    }

    private fun onEvent(event: Event) {
        logger.log("onEvent($event)")
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
