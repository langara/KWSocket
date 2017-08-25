package pl.elpassion.iotguard.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.example_ws_activity.*
import pl.elpassion.iot.api.messages
import pl.elpassion.iot.api.send
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R
import pl.elpassion.loggers.TextViewLogger
import pl.elpassion.loggers.logWifiDetails

class ExampleWSActivity : AppCompatActivity() {

    private val port = 9999
    private val uri = "ws://localhost:$port"

    private val logger by lazy { TextViewLogger(exampleLogsTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Guard") }

    private val server by lazy { DI.provideNewServer() }

    private val client1 by lazy { DI.provideNewClient() }
    private val client2 by lazy { DI.provideNewClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_ws_activity)
        DI.provideLogger = { logger }

        server.events.subscribe { logger.log("server got event: $it") }
        server.messages.subscribe { server.send(it) }
        server.start(port)

        client1.events.subscribe { logger.log("client1 got event: $it") }
        client2.events.subscribe { logger.log("client2 got event: $it") }

        logger.logWifiDetails(this)

        later(500) {
            client1.connect(uri)
            client2.connect(uri)
        }

        sendButton.setOnClickListener {
            later(500) { client1.send("Hi, it's client1") }
            later(600) { client2.send("Hi, it's client2") }
            later(700) { server.connections[0].send("Private msg to first client") }
            later(800) { server.connections[1].send("Private msg to second client") }
        }
    }

    private fun later(delay: Long, block: () -> Unit) {
        sendButton.postDelayed(block, delay)
    }
}
