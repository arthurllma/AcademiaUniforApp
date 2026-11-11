package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class IniciarTreinoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_treino)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        findViewById<View>(R.id.btnIniciarExercicio)?.setOnClickListener {
            startActivity(Intent(this, ExercicioAlunoActivity::class.java))
        }

        findViewById<View>(R.id.btnEditarExercicio)?.setOnClickListener {
            startActivity(Intent(this, EditarExercicioActivity::class.java))
        }

        findViewById<View>(R.id.btnOndeFica)?.setOnClickListener {
            startActivity(Intent(this, MinimapaAlunoActivity::class.java))
        }

        // Bottom nav
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeAlunoActivity::class.java))
            finish()
        }
    }
}
