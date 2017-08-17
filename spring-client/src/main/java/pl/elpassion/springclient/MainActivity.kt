package pl.elpassion.springclient

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.WebSocket
import ua.naiksoftware.stomp.Stomp


class MainActivity : AppCompatActivity() {

    private val stompClient = Stomp.over(WebSocket::class.java, "ws://192.168.1.44:8080/iotguard/websocket")
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        stompClient.run {
            connect()
            topic("/responses")
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({ topicMessage -> Log.e("!@#$%T", topicMessage.payload) })

        }
        leftButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> send(Command.GO_LEFT)
                MotionEvent.ACTION_UP -> send(Command.GO_LEFT_STOP)
            }
            false
        }


        rightButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> send(Command.GO_RIGHT)
                MotionEvent.ACTION_UP -> send(Command.GO_RIGHT_STOP)
            }
            false
        }

    }

    private fun send(command: Command) {
        stompClient.send("/app/commands", command.json)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private val Command.json: String
        get() = gson.toJson(this)
}


enum class Command {
    GO_LEFT,
    GO_LEFT_STOP,
    GO_RIGHT,
    GO_RIGHT_STOP
}
