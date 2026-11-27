package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.TriagemDataManager
import com.google.firebase.auth.FirebaseAuth

class TriagemEtapa2Activity : AppCompatActivity() {

    private var peso: Double = 70.0
    private var altura: Double = 175.0
    private var userId: String = ""
    private var isModoEdicao: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa2)

        isModoEdicao = intent.getBooleanExtra("MODO_EDICAO", false)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

        if (isModoEdicao) {
            findViewById<TextView>(R.id.tvHeaderTitulo)?.text = "EDITAR ANAMNESE"
            findViewById<TextView>(R.id.tvHeaderEtapa)?.text = "Etapa 2 de 4"
            findViewById<ProgressBar>(R.id.pbTriagem)?.apply {
                max = 4
                progress = 2
            }
        }

        val btnVoltar = findViewById<View>(R.id.btnVoltarTriagem)
        btnVoltar?.setOnClickListener { finish() }

        val etPeso = findViewById<EditText>(R.id.etPeso)
        val etAltura = findViewById<EditText>(R.id.etAltura)
        val tvImcValor = findViewById<TextView>(R.id.tvImcValor)
        val tvClassificacaoImc = findViewById<TextView>(R.id.tvClassificacaoImc)
        val cbPresencial = findViewById<CheckBox>(R.id.cbPresencial)

        fun updateImc() {
            if (cbPresencial.isChecked) {
                tvImcValor.text = "--"
                tvClassificacaoImc.text = "Preenchimento presencial"
                return
            }

            val p = etPeso.text.toString().toDoubleOrNull()
            val a = etAltura.text.toString().toDoubleOrNull()

            if (p != null && a != null && p > 0 && a > 0) {
                val alturaM = a / 100.0
                val imc = p / (alturaM * alturaM)
                tvImcValor.text = String.format("%.1f", imc)

                tvClassificacaoImc.text = when {
                    imc < 18.5 -> "Abaixo do peso"
                    imc < 25.0 -> "Peso normal (Saudável)"
                    imc < 30.0 -> "Sobrepeso"
                    else -> "Obesidade"
                }
            } else {
                tvImcValor.text = "--"
                tvClassificacaoImc.text = "Preencha os dados acima"
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateImc()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etPeso.addTextChangedListener(textWatcher)
        etAltura.addTextChangedListener(textWatcher)

        cbPresencial.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPeso.isEnabled = false
                etAltura.isEnabled = false
                updateImc()
            } else {
                etPeso.isEnabled = true
                etAltura.isEnabled = true
                updateImc()
            }
        }

        if (isModoEdicao) {
            if (TriagemDataManager.presencialBiometria || TriagemDataManager.peso.equals("Presencial", ignoreCase = true)) {
                cbPresencial.isChecked = true
            } else {
                if (TriagemDataManager.peso.isNotEmpty() && TriagemDataManager.peso != "0.0") {
                    val pVal = TriagemDataManager.peso.toDoubleOrNull()
                    if (pVal != null && pVal > 0) {
                        peso = pVal
                        etPeso.setText(if (pVal % 1.0 == 0.0) pVal.toInt().toString() else pVal.toString())
                    } else {
                        etPeso.setText(TriagemDataManager.peso)
                    }
                }
                if (TriagemDataManager.altura.isNotEmpty() && TriagemDataManager.altura != "0.0") {
                    val aVal = TriagemDataManager.altura.toDoubleOrNull()
                    if (aVal != null && aVal > 0) {
                        altura = aVal
                        etAltura.setText(if (aVal % 1.0 == 0.0) aVal.toInt().toString() else aVal.toString())
                    } else {
                        etAltura.setText(TriagemDataManager.altura)
                    }
                }
                updateImc()
            }
        }

        findViewById<Button>(R.id.btnPesoMinus)?.setOnClickListener {
            val currentP = etPeso.text.toString().toDoubleOrNull() ?: peso
            peso = (currentP - 0.5).coerceAtLeast(30.0)
            etPeso.setText(peso.toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnPesoPlus)?.setOnClickListener {
            val currentP = etPeso.text.toString().toDoubleOrNull() ?: peso
            peso = currentP + 0.5
            etPeso.setText(peso.toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnAlturaMinus)?.setOnClickListener {
            val currentA = etAltura.text.toString().toDoubleOrNull() ?: altura
            altura = (currentA - 1.0).coerceAtLeast(100.0)
            etAltura.setText(altura.toInt().toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnAlturaPlus)?.setOnClickListener {
            val currentA = etAltura.text.toString().toDoubleOrNull() ?: altura
            altura = currentA + 1.0
            etAltura.setText(altura.toInt().toString())
            updateImc()
        }

        findViewById<Button>(R.id.btnProsseguirTriagem2)?.setOnClickListener {
            if (!cbPresencial.isChecked) {
                val p = etPeso.text.toString().toDoubleOrNull()
                val a = etAltura.text.toString().toDoubleOrNull()

                if (p == null || p <= 0 || a == null || a <= 0) {
                    Toast.makeText(this, "Preencha peso e altura válidos ou marque a opção presencial.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                TriagemDataManager.peso = p.toString()
                TriagemDataManager.altura = a.toString()
                TriagemDataManager.imc = tvImcValor.text.toString()
                TriagemDataManager.presencialBiometria = false
            } else {
                TriagemDataManager.peso = "Presencial"
                TriagemDataManager.altura = "Presencial"
                TriagemDataManager.imc = "Presencial"
                TriagemDataManager.presencialBiometria = true
            }

            val nextIntent = Intent(this, TriagemEtapa3Activity::class.java)
            nextIntent.putExtra("USER_UID", userId)
            nextIntent.putExtra("user_id", userId)
            nextIntent.putExtra("MODO_EDICAO", isModoEdicao)
            startActivity(nextIntent)
        }
    }
}
