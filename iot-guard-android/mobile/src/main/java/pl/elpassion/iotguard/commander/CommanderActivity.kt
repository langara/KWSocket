package pl.elpassion.iotguard.commander

import android.os.Bundle
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.iotguard.TextViewLogger

class CommanderActivity : RxAppCompatActivity() {

    private val commander by lazy { DI.provideCommander() }

    private val logger by lazy { TextViewLogger(logsTextView, "IoT Guard") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commander_activity)
        initModel()
        forwardButton.setOnClickListener { commander.perform(MoveForward) }
        backwardButton.setOnClickListener { commander.perform(MoveBackward) }
        leftButton.setOnClickListener { commander.perform(MoveLeft) }
        rightButton.setOnClickListener { commander.perform(MoveRight) }
        stopButton.setOnClickListener { commander.perform(Stop) }
        connectButton.setOnClickListener { commander.perform(Connect(robotAddress.text.toString())) }
    }

    protected fun initModel() {
        commander.states
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { if(!isFinishing) showState(it) }
    }

    private fun showState(state: CommanderState) = logger.log("TODO: show state: $state")
}
