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

        fun setupExercicio(btnIniciarId: Int, btnEditarId: Int, btnOndeFicaId: Int, nomeExercicio: String) {
            findViewById<View>(btnIniciarId)?.setOnClickListener {
                val intent = Intent(this, ExercicioAlunoActivity::class.java)
                intent.putExtra("exercicio_nome", nomeExercicio)
                startActivity(intent)
            }

            findViewById<View>(btnEditarId)?.setOnClickListener {
                val intent = Intent(this, EditarExercicioActivity::class.java)
                intent.putExtra("exercicio_nome", nomeExercicio)
                startActivity(intent)
            }

            findViewById<View>(btnOndeFicaId)?.setOnClickListener {
                val intent = Intent(this, MinimapaAlunoActivity::class.java)
                intent.putExtra("exercicio_nome", nomeExercicio)
                startActivity(intent)
            }
        }

        setupExercicio(R.id.btnIniciarExercicio1, R.id.btnEditarExercicio1, R.id.btnOndeFica1, "Supino Reto")
        setupExercicio(R.id.btnIniciarExercicio2, R.id.btnEditarExercicio2, R.id.btnOndeFica2, "Supino Inclinado")
        setupExercicio(R.id.btnIniciarExercicio3, R.id.btnEditarExercicio3, R.id.btnOndeFica3, "Crucifixo")
        setupExercicio(R.id.btnIniciarExercicio4, R.id.btnEditarExercicio4, R.id.btnOndeFica4, "Tríceps Pulley")
        setupExercicio(R.id.btnIniciarExercicio5, R.id.btnEditarExercicio5, R.id.btnOndeFica5, "Tríceps Francês")

        // Bottom nav
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, HomeAlunoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
