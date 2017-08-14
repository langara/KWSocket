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

    private val model by lazy { DI.provideCommanderModel() }

    private val logger by lazy { TextViewLogger(logsTextView, "IoT Guard") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commander_activity)
        initModel()
        forwardButton.setOnClickListener { model.perform(Forward) }
        backwardButton.setOnClickListener { model.perform(Backward) }
        leftButton.setOnClickListener { model.perform(Left) }
        rightButton.setOnClickListener { model.perform(Right) }
        stopButton.setOnClickListener { model.perform(Stop) }
        connectButton.setOnClickListener { model.perform(Connect(robotAddress.text.toString())) }
    }

    protected fun initModel() {
        model.states
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { if(!isFinishing) showState(it) }
    }

    private fun showState(state: CommanderState) = logger.log("TODO: show state: $state")
}
