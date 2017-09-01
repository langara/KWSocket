package pl.elpassion.iot.alien

import android.app.Application
import pl.elpassion.loggers.AndroidLogger

class AlienApp : Application() {

    private val logger = AndroidLogger("IoT Alien")

    override fun onCreate() {
        super.onCreate()
        DI.provideApplication = { this }
        DI.provideLogger = { logger }
    }

    companion object {
        init {
//            WebSocketImpl.DEBUG = true
        }
    }
}