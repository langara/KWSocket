package pl.elpassion.iot.alien

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.WindowManager
import kotlinx.android.synthetic.main.alien_activity.*
import pl.elpassion.loggers.TextViewLogger

class AlienActivity : AppCompatActivity() {

    private val alien by lazy { DI.provideAlien() }

    private val logger by lazy { TextViewLogger(alienLogTextView.apply { movementMethod = ScrollingMovementMethod() }, "IoT Alien") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.alien_activity)
        DI.provideApplication = { application }
        DI.provideLogger = { logger }
        DI.provideActivity = { this }
        DI.provideSurfaceView = { surfaceView }
        alien.start()
    }

    override fun onDestroy() {
        alien.turnOff()
        super.onDestroy()
    }
}