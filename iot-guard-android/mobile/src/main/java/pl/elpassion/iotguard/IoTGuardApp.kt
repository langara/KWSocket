package pl.elpassion.iotguard

import android.app.Application
import android.util.Log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


class IoTGuardApp : Application() {

    private val logger = AndroidLogger("IoT Guard")

    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
        DI.provideLogger = { logger }
    }

    companion object {
        init {
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