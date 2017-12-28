package pl.elpassion.iot.robot

import android.content.Context
import android.speech.tts.TextToSpeech
import pl.elpassion.loggers.Logger
import java.util.*

class Babbler(context: Context, private val logger: Logger) : TextToSpeech.OnInitListener {

    private val tts: TextToSpeech = TextToSpeech(context, this)

    private var ttsready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            ttsready = true
            logger.log("Text to speech ready.")
        } else
            logger.log("Text to speech disabled.")
    }

    fun say(text: String, flush: Boolean = true) {
        logger.log(text)
        if (!ttsready) {
            logger.log("TTS not ready")
        }
        else {
            val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
            tts.speak(text, queueMode, null, null)
        }
    }

    fun shutdown() {
        ttsready = false
        tts.shutdown()
    }
}
