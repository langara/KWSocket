package pl.elpassion.iotguard

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


interface Model<ActionType, StateType> {

    val states: Observable<StateType>

    fun perform(action: ActionType)
}

interface CommanderModel : Model<CommanderAction, CommanderState>

sealed class CommanderAction

object Forward : CommanderAction()
object Backward : CommanderAction()
object Left : CommanderAction()
object Right : CommanderAction()

sealed class CommanderState

object Disconnected : CommanderState()
object Connected : CommanderState()



class CommanderModelImpl : CommanderModel {

    private val logger by lazy { DI.provideLogger() }

    private val statesSubject = BehaviorSubject.createDefault<CommanderState>(Disconnected)

    override val states: Observable<CommanderState> = statesSubject

    override fun perform(action: CommanderAction) {
        logger.log("TODO: perform action: $action")
    }
}
