package pl.elpassion.iotguard.commander

import io.reactivex.Observable


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
object Stop : CommanderAction()
data class Connect(val robotAddress: String) : CommanderAction()

sealed class CommanderState

object Disconnected : CommanderState()
object Connected : CommanderState()



