package pl.elpassion.iot.commander

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.touches
import com.jmedeisis.bugstick.Joystick
import com.jmedeisis.bugstick.JoystickListener
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*
import pl.elpassion.iot.commander.CommanderAction.*
import pl.elpassion.loggers.TextViewLogger
import pl.elpassion.loggers.logWifiDetails
import pl.elpassion.webrtc.WebRtcManager
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
        initWebRtc()
    }

    override fun onDestroy() {
        super.onDestroy()
        webRtcManager?.cancelAllCalls()
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
            touchpad.touchActions(),
            joystick.actions()
    ))

    private fun initWebRtc() {
        val username = UUID.randomUUID().toString().take(5)
        webRtcManager = WebRtcManager(this, surfaceView, this, username)
        webRtcManager?.startListening()
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

    companion object {

        private fun Joystick.actions() = Observable.create<CommanderAction> { emitter ->
            val listener = object : JoystickListener {
                override fun onDrag(degrees: Float, offset: Float) = emitter.onNext(MoveEnginesByJoystick(degrees.toInt(), (offset * offset).toDouble()))
                override fun onDown() = Unit
                override fun onUp() = emitter.onNext(Stop)
            }
            setJoystickListener(listener)
            emitter.setCancellable { setJoystickListener(null) }
        }

        private fun View.touchActions() = touches().map { it.toAction(width.toFloat(), height.toFloat()) }

        private fun MotionEvent.toAction(width: Float, height: Float) = if (action == MotionEvent.ACTION_UP) Stop else {
            var posx = x.coerceIn(-width / 2..width * 1.5f) + width / 2 // 0 (left) .. width * 2 (right)
            var posy = y.coerceIn(-height / 2..height * 1.5f) + height / 2 // 0 (top) .. height * 2 (bottom)
            posx = posx * 200 / (width * 2) - 100 // -100 (left) .. 100 (right)
            posy = posy * 200 / (height * 2) - 100 // -100 (top) .. 100 (bottom)
            posy = -posy // -100 (bottom) .. 100 (top)
            var left = (posx + posy).coerceIn(-100f..100f)
            var right = (posy - posx).coerceIn(-100f..100f)
            left *= Math.abs(left / 100) // more precision in the middle
            right *= Math.abs(right / 100) // more precision in the middle
            MoveWheels(left.toInt(), right.toInt())
        }
    }
}
