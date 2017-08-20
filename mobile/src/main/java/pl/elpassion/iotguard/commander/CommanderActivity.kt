package pl.elpassion.iotguard.commander

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger
import pl.elpassion.iotguard.commander.CommanderAction.*
import pl.elpassion.iotguard.logWifiDetails

class CommanderActivity : RxAppCompatActivity() {

    private val SPEECH_REQUEST_CODE = 77

    private val commander by lazy { DI.provideCommander() }

    private val logger by lazy { TextViewLogger(commanderLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    private val speechRecognizer by lazy { SpeechRecognizer(commander, logger) }

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
        logger.logWifiDetails(this)
    }

    protected fun initCommander() {
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
}
