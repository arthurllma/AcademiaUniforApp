package br.com.academiaunifortelas1

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import java.util.Locale

import android.os.Handler
import android.os.Looper

class ExercicioAlunoActivity : AppCompatActivity() {

    private var currentSerie = 1
    private val totalSeries = 4
    private var isTimerRunning = false
    
    // Handlers for progressive timers
    private var secondsElapsed = 0
    private var totalSecondsElapsed = 0
    private val timerHandler = Handler(Looper.getMainLooper())

    private lateinit var tvTimer: TextView
    private lateinit var tvTempoTotal: TextView
    private lateinit var btnIniciarSerie: Button
    private lateinit var tvSerie: TextView

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isTimerRunning) {
                secondsElapsed++
                totalSecondsElapsed++
                
                updateTimerText(tvTimer, secondsElapsed)
                updateTimerText(tvTempoTotal, totalSecondsElapsed)
                
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercicio_aluno)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }
        
        findViewById<View>(R.id.btnVerExemplo)?.setOnClickListener {
            startActivity(Intent(this, ExemplosAlunoActivity::class.java))
        }

        tvSerie = findViewById(R.id.tvSerieTitle)
        tvTimer = findViewById(R.id.tvTimer)
        tvTempoTotal = findViewById(R.id.tvTempoTotal)
        btnIniciarSerie = findViewById(R.id.btnIniciarSerie)

        tvSerie.text = getString(R.string.serie_format, currentSerie)
        updateTimerText(tvTimer, 0)
        updateTimerText(tvTempoTotal, 0)

        btnIniciarSerie.setOnClickListener {
            if (!isTimerRunning) {
                // Iniciar contagem progressiva
                if (currentSerie <= totalSeries) {
                    isTimerRunning = true
                    btnIniciarSerie.text = "Concluir Série"
                    timerHandler.postDelayed(timerRunnable, 1000)
                }
            } else {
                // Concluir série atual
                isTimerRunning = false
                timerHandler.removeCallbacks(timerRunnable)
                
                if (currentSerie < totalSeries) {
                    currentSerie++
                    tvSerie.text = getString(R.string.serie_format, currentSerie)
                    btnIniciarSerie.text = "Iniciar Série"
                    // Zera o tempo da série atual, mas mantém o total
                    secondsElapsed = 0
                    updateTimerText(tvTimer, 0)
                } else {
                    tvSerie.text = "Concluído!"
                    btnIniciarSerie.text = "Treino Finalizado"
                    btnIniciarSerie.isEnabled = false
                }
            }
        }

        findViewById<Button>(R.id.btnConcluirExercicio)?.setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnProximoExercicio)?.setOnClickListener {
            finish() // Em um fluxo real, navegaria para o próximo
        }
    }

    private fun updateTimerText(textView: TextView, seconds: Int) {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        textView.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        isTimerRunning = false
        timerHandler.removeCallbacks(timerRunnable)
    }
}
