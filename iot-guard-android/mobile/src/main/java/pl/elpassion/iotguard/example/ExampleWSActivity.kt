package pl.elpassion.iotguard.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.example_ws_activity.*
import pl.elpassion.iotguard.DI
import pl.elpassion.iotguard.R

class ExampleWSActivity : AppCompatActivity() {

    private val port = 9999

    private val logger by lazy { DI.provideLogger() }

    private val server by lazy { DI.provideNewServer(port) }

    private val client1 by lazy { DI.provideNewClient("ws://localhost:$port") }
    private val client2 by lazy { DI.provideNewClient("ws://localhost:$port") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_ws_activity)

        server.events.subscribe { logger.log("server got event: $it") }
        server.messages.subscribe { server.send(it) }
        server.start()

        client1.events.subscribe { logger.log("client1 got event: $it") }
        client2.events.subscribe { logger.log("client2 got event: $it") }
        connectButton.setOnClickListener {
            connectButton.isEnabled = false
            client1.connect()
            client2.connect()
            later(300) { client1.send("Hi, it's client1") }
            later(600) { client2.send("Hi, it's client2") }
            later(700) { server.connections[0].send("Private msg to first client") }
            later(800) { server.connections[1].send("Private msg to second client") }
        }
    }

    private fun later(delay: Long, block: () -> Unit) {
        connectButton.postDelayed(block, delay)
    }
}
