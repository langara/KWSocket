package pl.elpassion.loggers

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
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
            val line = "$tag $message\n"
            if (textView.text.length > 2000) {
                textView.text = line
            }
            else {
                textView.append(line)
            }
        }
    }
}

@SuppressLint("MissingPermission")
fun Logger.logWifiDetails(context: Context) {
  val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
  val info = manager.connectionInfo
  val addr = info.ipAddress.toStringIpAddr()
  log("Wifi info: $info")
  log("Wifi ip address: $addr")
}

private fun Int.toStringIpAddr() = "${this and 0xff}.${this shr 8 and 0xff}.${this shr 16 and 0xff}.${this shr 24 and 0xff}"
