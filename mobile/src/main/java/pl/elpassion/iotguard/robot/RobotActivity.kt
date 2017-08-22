package pl.elpassion.iotguard.robot

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import pl.elpassion.iotguard.DI

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        robot.start(9999)
    }

    override fun onDestroy() {
        robot.turnOff()
        super.onDestroy()
    }

}