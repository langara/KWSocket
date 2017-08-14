package pl.elpassion.iotguard.commander

import io.reactivex.Observable


interface Commander {
    val states : Observable<CommanderState>
    fun perform(action: CommanderAction)
}

sealed class CommanderAction

object MoveForward : CommanderAction()
object MoveBackward : CommanderAction()
object MoveLeft : CommanderAction()
object MoveRight : CommanderAction()
object Stop : CommanderAction()
data class Connect(val robotAddress: String) : CommanderAction()

sealed class CommanderState

object Disconnected : CommanderState()
object Connected : CommanderState()



