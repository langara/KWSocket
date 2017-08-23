package pl.elpassion.iotguard.commander

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import com.jmedeisis.bugstick.JoystickListener
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger
import pl.elpassion.iotguard.commander.CommanderAction.*
import pl.elpassion.iotguard.logWifiDetails
import pl.elpassion.iotguard.streaming.WebRtcManager
import java.util.*

class CommanderActivity : RxAppCompatActivity(), WebRtcManager.ConnectionListener {

    private val SPEECH_REQUEST_CODE = 77

    private val commander by lazy { DI.provideCommander() }

    private val logger by lazy { TextViewLogger(commanderLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    private val speechRecognizer by lazy { SpeechRecognizer(commander, logger) }

    private var webRtcManager: WebRtcManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commander_activity)
        DI.provideLogger = { logger }
        initCommander()
        forwardButton.setOnClickListener { commander.perform(MoveForward) }
        backwardButton.setOnClickListener { commander.perform(MoveBackward) }
        leftButton.setOnClickListener { commander.perform(MoveLeft) }
        rightButton.setOnClickListener { commander.perform(MoveRight) }
        stopButton.setOnClickListener { commander.perform(Stop) }
        connectButton.setOnClickListener { commander.perform(Connect(robotAddress.text.toString())) }
        listenButton.setOnClickListener { speechRecognizer.start(this, SPEECH_REQUEST_CODE) }
        touchpad.setOnTouchListener(this::onTouch)
        logger.logWifiDetails(this)
        if (intent?.extras?.containsKey("KEY_HANDOVER_THROUGH_VELVET") ?: false) {
            // app started with voice command, so we immediately listen for some more commands
            listenButton.performClick()
        }
        joystick.setJoystickListener(object : JoystickListener {
            override fun onDrag(degrees: Float, offset: Float) {
                commander.perform(MoveEnginesByJoystick(degrees.toInt(), offset.toDouble()))
            }

            override fun onDown() {

            }

            override fun onUp() {
                commander.perform(MoveEnginesByJoystick(0, 0.0))
            }
        })
        initWebRtc()
    }

    private fun initWebRtc() {
        val username = UUID.randomUUID().toString().take(5)
        webRtcManager = WebRtcManager(this, surfaceView, this, username)
        webRtcManager?.startListening()
        webRtcManager?.callUser("ALIEN")
    }

    private fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == MotionEvent.ACTION_UP) {
            commander.perform(Stop)
            return true
        }
        val width = view.width.toFloat()
        val height = view.height.toFloat()
        var x = motionEvent.x.coerceIn(-width/2..width*1.5f) + width/2 // from 0 (left) to width * 2 (right)
        var y = motionEvent.y.coerceIn(-height/2..height*1.5f) + height/2 // from 0 (top) to height * 2 (bottom)
        x = x * 200 / (width * 2) - 100 // -100 (left) .. 100 (right)
        y = y * 200 / (height * 2) - 100 // -100 (top) .. 100 (bottom)
        y = -y // -100 (bottom) .. 100 (top)
        val left = (x + y).coerceIn(-100f..100f)
        val right = (y - x).coerceIn(-100f..100f)
        commander.perform(MoveWheels(left.toInt(), right.toInt()))
        return true
    }

    private fun initCommander() {
        commander.states
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { if (!isFinishing) showState(it) }
    }

    private fun showState(state: CommanderState) = logger.log("State: $state")

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SPEECH_REQUEST_CODE)
            speechRecognizer.handleSpeechRecognizerActivityResult(resultCode, data)
        else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onConnecting(remoteUser: String) = Unit

    override fun onConnected(remoteUser: String) = Unit

    override fun onDisconnected(remoteUser: String) = Unit
}
