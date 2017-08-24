package pl.elpassion.iotguard.commander

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.touches
import com.jmedeisis.bugstick.JoystickListener
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger
import pl.elpassion.iotguard.commander.CommanderAction.*
import pl.elpassion.iotguard.logWifiDetails
import pl.elpassion.iotguard.streaming.WebRtcManager
import java.util.*
import java.util.concurrent.TimeUnit

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
        mergeActions()
                .sample(100, TimeUnit.MILLISECONDS)
                .bindToLifecycle(this)
                .subscribe(commander.actions)

        listenButton.setOnClickListener { speechRecognizer.start(this, SPEECH_REQUEST_CODE) }
        logger.logWifiDetails(this)
        if (intent?.extras?.containsKey("KEY_HANDOVER_THROUGH_VELVET") ?: false) {
            // app started with voice command, so we immediately listen for some more commands
            listenButton.performClick()
        }
        joystick.setJoystickListener(object : JoystickListener {
            override fun onDrag(degrees: Float, offset: Float) {
                commander.actions.accept(MoveEnginesByJoystick(degrees.toInt(), offset.toDouble()))
            }

            override fun onDown() {

            }

            override fun onUp() {
                commander.actions.accept(MoveEnginesByJoystick(0, 0.0))
            }
        })
        initWebRtc()
    }

    private fun mergeActions() = Observable.merge(listOf(
            forwardButton.clicks().map { MoveForward },
            backwardButton.clicks().map { MoveBackward },
            leftButton.clicks().map { MoveLeft },
            rightButton.clicks().map { MoveRight },
            stopButton.clicks().map { Stop },
            connectButton
                    .clicks()
                    .map { Connect(robotAddress.text.toString()) }
                    .doOnNext { webRtcManager?.callUser("ALIEN") },
            touchpad.touches().map { it.toAction() }
    ))

    private fun initWebRtc() {
        val username = UUID.randomUUID().toString().take(5)
        webRtcManager = WebRtcManager(this, surfaceView, this, username)
        webRtcManager?.startListening()
    }

    private fun MotionEvent.toAction(): CommanderAction {
        if (action == MotionEvent.ACTION_UP) {
            return Stop
        }
        val width = touchpad.width.toFloat()
        val height = touchpad.height.toFloat()
        var posx = x.coerceIn(-width / 2..width * 1.5f) + width / 2 // 0 (left) .. width * 2 (right)
        var posy = y.coerceIn(-height / 2..height * 1.5f) + height / 2 // 0 (top) .. height * 2 (bottom)
        posx = posx * 200 / (width * 2) - 100 // -100 (left) .. 100 (right)
        posy = posy * 200 / (height * 2) - 100 // -100 (top) .. 100 (bottom)
        posy = -posy // -100 (bottom) .. 100 (top)
        var left = (posx + posy).coerceIn(-100f..100f)
        var right = (posy - posx).coerceIn(-100f..100f)
        left *= Math.abs(left / 100) // more precision in the middle
        right *= Math.abs(right / 100) // more precision in the middle
        return MoveWheels(left.toInt(), right.toInt())
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
