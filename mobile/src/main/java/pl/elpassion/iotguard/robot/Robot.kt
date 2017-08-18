package pl.elpassion.iotguard.robot

import io.reactivex.Observable

interface Robot {
    val states : Observable<RobotState>
    fun perform(action: RobotAction)
}