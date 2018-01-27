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

    override val stateS: BehaviorRelay<CommanderState> = BehaviorRelay.createDefault<CommanderState>(Disconnected)
    override val actionS: PublishRelay<CommanderAction> = PublishRelay.create<CommanderAction>()

    init {
        client.eventS.subscribe(this::onEvent)
        actionS.subscribe(this::call)
    }

    private fun call(action: CommanderAction) {
        logger.log("perform($action)")
        when (action) {
            is Connect -> client.connect(action.serverAddress)
            Disconnect -> client.close()
            is Recognize -> recognize(action.speech)
            MoveForward -> client.send("move forward")
            MoveBackward -> client.send("move backward")
            MoveLeft -> client.send("move left")
            MoveRight -> client.send("move right")
            is MoveWheels -> client.send("move wheels ${action.left} ${action.right}")
            Stop -> client.send("stop")
            is Say -> client.send("say ${action.sentence}")
            LookUp -> client.send("look up")
            LookDown -> client.send("look down")
            LookAhead -> client.send("look ahead")
            is ChangeVolume -> client.send("volume ${action.delta}")
        }
    }

    private fun onEvent(event: Event) {
        logger.log("onEvent($event)")
        when (event) {
            is Open -> stateS.accept(Connected)
            is Close -> stateS.accept(Disconnected)
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
        action?.let { actionS.accept(it) } ?: logger.log("I don't understand: $speech")
    }

    private fun List<String>.matchesCommand(vararg commands: String) = any { recognized ->
        commands.any { command ->
            recognized.contains(command, ignoreCase = true)
        }
    }
}
