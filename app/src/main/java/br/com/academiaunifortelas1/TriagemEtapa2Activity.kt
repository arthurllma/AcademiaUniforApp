package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TriagemEtapa2Activity : AppCompatActivity() {

    private var peso = 78.5
    private var altura = 178.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa2)

        val etPeso = findViewById<EditText>(R.id.etPeso)
        val etAltura = findViewById<EditText>(R.id.etAltura)
        val tvImcValor = findViewById<TextView>(R.id.tvImcValor)
        val tvClassificacaoImc = findViewById<TextView>(R.id.tvClassificacaoImc)

        fun updateImc() {
            val p = etPeso.text.toString().toDoubleOrNull() ?: peso
            val a = etAltura.text.toString().toDoubleOrNull() ?: altura
            if (a > 0) {
                val alturaM = a / 100.0
                val imc = p / (alturaM * alturaM)
                tvImcValor.text = String.format("%.1f", imc)

                tvClassificacaoImc.text = when {
                    imc < 18.5 -> "Abaixo do peso"
                    imc < 25.0 -> "Peso normal (Saudável)"
                    imc < 30.0 -> "Sobrepeso"
                    else -> "Obesidade"
                }
            }
        }

        findViewById<Button>(R.id.btnPesoMinus)?.setOnClickListener {
            peso = (peso - 0.5).coerceAtLeast(30.0)
            etPeso.setText(peso.toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnPesoPlus)?.setOnClickListener {
            peso += 0.5
            etPeso.setText(peso.toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnAlturaMinus)?.setOnClickListener {
            altura = (altura - 1.0).coerceAtLeast(100.0)
            etAltura.setText(altura.toInt().toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnAlturaPlus)?.setOnClickListener {
            altura += 1.0
            etAltura.setText(altura.toInt().toString())
            updateImc()
        }

        // Prosseguir para a próxima etapa (Etapa 3)
        findViewById<Button>(R.id.btnProsseguirTriagem2)?.setOnClickListener {
            startActivity(Intent(this, TriagemEtapa3Activity::class.java))
            // Não damos finish() aqui para que o usuário possa voltar para a Etapa 1 e 2 se quiser alterar
        }
    }
}
