package pl.elpassion.iotguard

import android.util.Log
import android.widget.TextView


interface Logger {
    fun log(message: Any?)
}

class SimpleLogger : Logger {
    override fun log(message: Any?) = println(message)
}

class AndroidLogger(private val tag: String) : Logger {
    override fun log(message: Any?) {
        Log.w(tag, message.toString())
    }
}

class TextViewLogger(private val textView: TextView, private val tag: String) : Logger {
    override fun log(message: Any?) {
        Log.w(tag, message.toString())
        textView.post {
            textView.append("$tag $message\n")
        }
    }
}