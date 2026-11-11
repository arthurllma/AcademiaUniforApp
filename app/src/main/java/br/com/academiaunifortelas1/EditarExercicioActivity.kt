package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EditarExercicioActivity : AppCompatActivity() {

    private var series = 4
    private var reps = 12
    private var carga = 60

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_exercicio)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnVoltarExercicio)?.setOnClickListener {
            finish()
        }

        val spDescanso = findViewById<Spinner>(R.id.spDescanso)
        val descansoOptions = arrayOf("30s", "45s", "60s", "90s", "120s")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, descansoOptions)
        spDescanso?.adapter = adapter
        spDescanso?.setSelection(2) // 60s

        val tvSeries = findViewById<TextView>(R.id.tvSeries)
        findViewById<View>(R.id.btnMinusSeries)?.setOnClickListener {
            if (series > 1) {
                series--
                tvSeries?.text = series.toString()
            }
        }
        findViewById<View>(R.id.btnPlusSeries)?.setOnClickListener {
            series++
            tvSeries?.text = series.toString()
        }

        val tvReps = findViewById<TextView>(R.id.tvReps)
        findViewById<View>(R.id.btnMinusReps)?.setOnClickListener {
            if (reps > 1) {
                reps--
                tvReps?.text = reps.toString()
            }
        }
        findViewById<View>(R.id.btnPlusReps)?.setOnClickListener {
            reps++
            tvReps?.text = reps.toString()
        }

        val tvCarga = findViewById<TextView>(R.id.tvCarga)
        findViewById<View>(R.id.btnMinusCarga)?.setOnClickListener {
            if (carga > 0) {
                carga -= 5
                if (carga < 0) carga = 0
                tvCarga?.text = carga.toString()
            }
        }
        findViewById<View>(R.id.btnPlusCarga)?.setOnClickListener {
            carga += 5
            tvCarga?.text = carga.toString()
        }

        findViewById<Button>(R.id.btnSalvarAlteracoes)?.setOnClickListener {
            Toast.makeText(this, "Alterações salvas com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<View>(R.id.btnCancelarEdit)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navTreinos)?.setOnClickListener {
            startActivity(Intent(this, TreinoActivity::class.java))
            finish()
        }
    }
}
