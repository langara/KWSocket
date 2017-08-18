package pl.elpassion.iotguard.robot

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import pl.elpassion.iotguard.Logger
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ttsready)
            tts.speak(text, if(flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, null)
    }

    fun shutdown() {
        ttsready = false
        tts.shutdown()
    }
}
