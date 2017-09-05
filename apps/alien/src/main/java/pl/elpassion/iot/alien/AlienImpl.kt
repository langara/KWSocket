package pl.elpassion.iot.alien

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import pl.elpassion.iot.api.*
import pl.elpassion.loggers.Logger

class AlienImpl(private val server: Server, private val client: Client, private val babbler: Babbler, private val context: Context, private val logger: Logger) : Alien {

    companion object {
        const val VOLUME_COMMAND_PREFIX = "volume "
        const val SAY_COMMAND_PREFIX = "say "
    }


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
            is Open -> say("connected")
            is Close -> say("disconnected")
        }
    }

    private fun onMessage(message: String) {
        when {
            message.startsWith(SAY_COMMAND_PREFIX) -> say(message.substring(SAY_COMMAND_PREFIX.length))
            message.startsWith(VOLUME_COMMAND_PREFIX) -> changeVolume(message.substring(VOLUME_COMMAND_PREFIX.length).toInt())
            else -> delegateToRobot(message)
        }
    }

    private fun changeVolume(delta: Int) {
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = manager.getStreamVolume(STREAM_MUSIC)
        val newVolume = (currentVolume + delta).coerceIn(0, manager.getStreamMaxVolume(STREAM_MUSIC))
        manager.setStreamVolume(STREAM_MUSIC, newVolume, 0)
    }

    private fun delegateToRobot(message: String) {
        client.send(message)
        when (message) {
            "move forward", "move backward", "move left", "move right", "stop" -> {
                babbler.say(randomReadyConfirmation)
            }
        }
    }

    private fun say(speech: String) {
        when (speech) {
            in listOf("something funny", "something stupid") -> babbler.say(randomFunnySentence)
            in listOf("something smart", "something intelligent") -> babbler.say(randomSmartSentence)
            else -> babbler.say(speech)
        }
    }

    private val randomFunnySentence
        get() = FUNNY_QUOTES.random().sentence

    private val randomSmartSentence
        get() = SMART_QUOTES.random().let { (sentence, author) -> "$sentence\n$author" }

    private val randomReadyConfirmation
        get() = READY_CONFIRMATIONS.random()
}