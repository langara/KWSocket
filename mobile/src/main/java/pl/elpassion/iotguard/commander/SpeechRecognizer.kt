package pl.elpassion.iotguard.commander

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.support.v7.app.AppCompatActivity
import pl.elpassion.iotguard.Logger


class SpeechRecognizer(private val commander: Commander, private val logger: Logger) {

    fun start(activity: AppCompatActivity, speechRequestCode: Int) {
        val intent = createIntent()
        if (intent.resolveActivity(activity.packageManager) != null) {
            try {
                activity.startActivityForResult(intent, speechRequestCode)
            } catch (e: ActivityNotFoundException) {
                logger.log("Speech recognizer not found: $e")
            } catch (e: SecurityException) {
                logger.log("Security exception: $e")
            }

        } else {
            logger.log("No activity found for this intent: $intent")
        }
    }

    fun handleSpeechRecognizerActivityResult(resultCode: Int, data: Intent?) = when (resultCode) {
        Activity.RESULT_OK -> {
            val results = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            //                float[] scores = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
            //                log.v("Voice recognition results:");
            //                for(int i = 0; i < results.size(); ++i) {
            //                    String result = results.get(i);
            //                    float score = scores == null ? -1 : scores[i];
            //                    log.v("   %f:%s", score, result);
            //                }
            val speech = results[0].toLowerCase()

            commander.actions.accept(CommanderAction.Recognize(speech))
        }
        Activity.RESULT_CANCELED -> logger.log("Voice recognition cancelled.")
        RecognizerIntent.RESULT_AUDIO_ERROR -> logger.log("Voice recognition: audio error.")
        RecognizerIntent.RESULT_CLIENT_ERROR -> logger.log("Voice recognition: generic client error.")
        RecognizerIntent.RESULT_NETWORK_ERROR -> logger.log("Voice recognition: network error.")
        RecognizerIntent.RESULT_NO_MATCH -> logger.log("Voice recognition: no match.")
        RecognizerIntent.RESULT_SERVER_ERROR -> logger.log("Voice recognition: server error.")
        else -> logger.log("Voice recognition: error code: $resultCode")
    }

    private fun createIntent() = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_PROMPT, "What's your command?")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US")
    }
}
