package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TreinoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_treino)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }
        
        findViewById<Button>(R.id.btnIniciarTreino)?.setOnClickListener {
            startActivity(Intent(this, IniciarTreinoActivity::class.java))
        }

        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeAlunoActivity::class.java))
            finish()
        }
    }
}
