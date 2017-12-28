package pl.elpassion.iot.robot

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.robot_activity.robotLogTextView
import pl.elpassion.loggers.TextViewLogger

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    private val logger by lazy { TextViewLogger(robotLogTextView.apply { movementMethod = ScrollingMovementMethod() }, "Robot") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_activity)
        DI.provideLogger = { logger }
        DI.provideApplication = { application }
        robot.start()
    }

    override fun onDestroy() {
        robot.turnOff()
        super.onDestroy()
    }

}