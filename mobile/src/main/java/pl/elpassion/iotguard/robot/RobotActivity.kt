package pl.elpassion.iotguard.robot

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.robot_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger
import pl.elpassion.iotguard.logWifiDetails

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    private val logger by lazy { TextViewLogger(robotLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_activity)
        DI.provideLogger = { logger }
        robot.start(9999)
        logger.logWifiDetails(this)
        val manager = PeripheralManagerService()
        val portList = manager.getGpioList()
        logger.log("List of available ports: $portList")
        val gpio2 = manager.openGpio("BCM2").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH)}
        val gpio3 = manager.openGpio("BCM3").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH)}
        val gpio23 = manager.openGpio("BCM23").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH)}
        val gpio24 = manager.openGpio("BCM24").apply { setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW); setActiveType(Gpio.ACTIVE_HIGH)}
        for(i in 1..8)
            later( i * 1500) {
                logger.log("changing gpio ports (i=$i)")
                gpio2.value = i % 2 == 0
                gpio2.value = i % 2 == 1
                gpio3.value = i % 2 == 1
                gpio3.value = i % 2 == 0
                gpio23.value = i % 2 == 1
                gpio23.value = i % 2 == 0
                gpio24.value = i % 2 == 0
                gpio24.value = i % 2 == 1
            }
        later( 9 * 1500) {
            gpio2.value = false
            gpio3.value = false
            gpio23.value = false
            gpio24.value = false

        }
    }

    private fun later(delay: Int, block: () -> Unit) {
        robotLogsTextView.postDelayed(block, delay.toLong())
    }
}