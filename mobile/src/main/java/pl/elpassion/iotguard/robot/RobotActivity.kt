package pl.elpassion.iotguard.robot

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.text.method.ScrollingMovementMethod
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.robot_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger

class RobotActivity : RxAppCompatActivity() {

    private val robot by lazy { DI.provideRobot() }

    private val logger by lazy { TextViewLogger(robotLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_activity)
        DI.provideLogger = { logger }
        robot.perform(RobotAction.Start(9999))
        logWiFiDetails()
    }

    private fun logWiFiDetails() {
        val manager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        val addr = Formatter.formatIpAddress(info.ipAddress)
        logger.log("Wifi info: $info")
        logger.log("Wifi ip address: $addr")
    }
}