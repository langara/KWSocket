package pl.elpassion.iotguard.commander

import io.reactivex.Observable


interface Commander {
    val states : Observable<CommanderState>
    fun perform(action: CommanderAction)
}



