package pl.elpassion.iotguard

import android.os.Bundle
import android.util.Log
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.commander_activity.*

class CommanderActivity : RxAppCompatActivity() {

    private val model by lazy { DI.provideCommanderModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.commander_activity)
        initModel()
        forward.setOnClickListener { model.perform(Forward) }
        backward.setOnClickListener { model.perform(Backward) }
        left.setOnClickListener { model.perform(Left) }
        right.setOnClickListener { model.perform(Right) }
    }

    protected fun initModel() {
        model.states
                .bindToLifecycle(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!isFinishing) {
                        showState(it)
                    }
                }
    }

    private fun showState(state: CommanderState?) {
        Log.w("IoT Commander Activity", "TODO: show state: $state")
    }
}
