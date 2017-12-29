package pl.elpassion.iot.commander

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import pl.elpassion.loggers.AndroidLogger

class CommanderApp : Application() {

    companion object {
        init {
//            WebSocketImpl.DEBUG = true
            setupRxJavaErrorHandler()
        }

        fun setupRxJavaErrorHandler() {
            RxJavaPlugins.setErrorHandler { throwable ->
                if (throwable is UndeliverableException) {
                    Log.v("RxJavaPlugins", "error handler got: $throwable")
                } else throw throwable
            }
        }
    }
}