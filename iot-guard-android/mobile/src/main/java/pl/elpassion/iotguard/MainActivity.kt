package pl.elpassion.iotguard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        ChatServer.launch(9999)
        connect.setOnClickListener {
            ExampleClient.launch()
            connect.isEnabled = false
        }
    }
}
