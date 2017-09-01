package pl.elpassion.iot.alien

import pl.elpassion.iot.api.*
import pl.elpassion.loggers.Logger

class AlienImpl(private val server: Server, private val client: Client, private val babbler: pl.elpassion.iot.alien.Babbler, private val logger: Logger) : pl.elpassion.iot.alien.Alien {

    override fun start() {
        server.start()
        server.events.subscribe { onEvent(it) }
        client.connect("ws://192.168.1.38:9999") // TODO: remove hardcoded robot address
        client.events.subscribe { logger.log("Alien.robotEvent($it)") }
    }

    override fun turnOff() {
        client.disconnect()
        server.disconnect()
    }

    private fun onEvent(event: Event) {
        logger.log("onEvent($event)")
        when (event) {
            is Message -> onMessage(event.message)
        }
    }

    private fun onMessage(message: String) {
        if (message.startsWith("say ")) {
            say(message.substring(4))
        }
        else {
            client.send(message)
            when (message) {
                "move forward", "move backward", "move left", "move right", "stop" -> {
                    say("Yes sir! $message.")
                }
            }
        }
    }

    private fun say(speech: String) {
        babbler.say(speech)
    }

}