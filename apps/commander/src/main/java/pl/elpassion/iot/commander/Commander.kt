package pl.elpassion.iot.commander

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay

interface Commander {
    val actions : PublishRelay<CommanderAction>
    val states : BehaviorRelay<CommanderState>
}



