package pl.elpassion.iotguard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.example_ws_activity.*

class ExampleWSActivity : AppCompatActivity() {

    private val logger by lazy { DI.provideLogger() }

    private val client by lazy { DI.provideClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_ws_activity)
        ExampleServer.launch(9999)
        client.events.subscribe { logger.log("ClientImpl got event: $it") }
        connect.setOnClickListener {
            client.connect()
            connect.isEnabled = false
        }
    }
}
