package pl.elpassion.iot.robot

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        robot.start()
    }

    override fun onDestroy() {
        robot.turnOff()
        super.onDestroy()
    }

}