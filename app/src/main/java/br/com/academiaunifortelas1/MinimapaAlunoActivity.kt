package br.com.academiaunifortelas1

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MinimapaAlunoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_minimapa_aluno)
        
        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }
    }
}
