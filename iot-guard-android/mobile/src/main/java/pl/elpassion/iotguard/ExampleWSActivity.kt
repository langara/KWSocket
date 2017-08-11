package pl.elpassion.iotguard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.example_ws_activity.*

class ExampleWSActivity : AppCompatActivity() {

    private val logger by lazy { DI.provideLogger() }

    private val client1 by lazy { DI.provideNewClient("ws://localhost:9999") }
    private val client2 by lazy { DI.provideNewClient("ws://localhost:9999") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_ws_activity)
        ExampleServer.launch(9999)
        client1.events.subscribe { logger.log("client1 got event: $it") }
        client2.events.subscribe { logger.log("client2 got event: $it") }
        connect.setOnClickListener {
            client1.connect()
            client2.connect()
            connect.isEnabled = false
        }
    }
}
