package pl.elpassion.iotguard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.example_ws_activity.*

class ExampleWSActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_ws_activity)
        ExampleServer.launch(9999)
        connect.setOnClickListener {
            ExampleClient.launch()
            connect.isEnabled = false
        }
    }
}
