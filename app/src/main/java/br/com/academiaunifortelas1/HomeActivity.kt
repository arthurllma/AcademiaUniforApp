package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnVerFichaTreino)?.setOnClickListener {
            startActivity(Intent(this, TreinoActivity::class.java))
        }

        findViewById<View>(R.id.navTreinos)?.setOnClickListener {
            startActivity(Intent(this, TreinoActivity::class.java))
            finish()
        }
    }
}
