package pl.elpassion.iot.robot

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import pl.elpassion.loggers.AndroidLogger

class RobotApp : Application() {

    private val logger = AndroidLogger("IoT Robot")

    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
        DI.provideLogger = { logger }
    }

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