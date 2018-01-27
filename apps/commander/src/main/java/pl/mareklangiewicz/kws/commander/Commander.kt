package pl.mareklangiewicz.kws.commander

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay

interface Commander {
    val actionS: PublishRelay<CommanderAction>
    val stateS: BehaviorRelay<CommanderState>
}



