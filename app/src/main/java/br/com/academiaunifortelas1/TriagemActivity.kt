package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.TriagemDataManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TriagemActivity : AppCompatActivity() {

    private var selectedExp: TextView? = null
    private var selectedObj: TextView? = null
    private var selectedFreq: TextView? = null
    private var userId: String = ""
    private var isModoEdicao: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem)

        isModoEdicao = intent.getBooleanExtra("MODO_EDICAO", false)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

        if (isModoEdicao) {
            findViewById<TextView>(R.id.tvHeaderTitulo)?.text = "EDITAR ANAMNESE"
            findViewById<TextView>(R.id.tvHeaderEtapa)?.text = "Etapa 1 de 4"
            findViewById<ProgressBar>(R.id.pbTriagem)?.apply {
                max = 4
                progress = 1
            }
            val btnVoltar = findViewById<View>(R.id.btnVoltarTriagem)
            btnVoltar?.visibility = View.VISIBLE
            btnVoltar?.setOnClickListener { finish() }
        }

        val chipExp1 = findViewById<TextView>(R.id.chipExp1)
        val chipExp2 = findViewById<TextView>(R.id.chipExp2)
        val chipExp3 = findViewById<TextView>(R.id.chipExp3)
        val expList = listOf(chipExp1, chipExp2, chipExp3)

        expList.forEach { chip ->
            chip.setOnClickListener {
                expList.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#0F172A"))
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
                selectedExp = chip
            }
        }

        val chipObj1 = findViewById<TextView>(R.id.chipObj1)
        val chipObj2 = findViewById<TextView>(R.id.chipObj2)
        val chipObj3 = findViewById<TextView>(R.id.chipObj3)
        val chipObj4 = findViewById<TextView>(R.id.chipObj4)
        val chipObj5 = findViewById<TextView>(R.id.chipObj5)
        val chipObj6 = findViewById<TextView>(R.id.chipObj6)
        val etOutro = findViewById<EditText>(R.id.etOutroObjetivo)
        val objList = listOf(chipObj1, chipObj2, chipObj3, chipObj4, chipObj5, chipObj6)

        objList.forEach { chip ->
            chip.setOnClickListener {
                objList.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#0F172A"))
                }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
                selectedObj = chip

                if (chip == chipObj6) {
                    etOutro.visibility = View.VISIBLE
                } else {
                    etOutro.visibility = View.GONE
                }
            }
        }

        val freqList = listOf(
            findViewById<TextView>(R.id.freq1),
            findViewById<TextView>(R.id.freq2),
            findViewById<TextView>(R.id.freq3),
            findViewById<TextView>(R.id.freq4),
            findViewById<TextView>(R.id.freq5),
            findViewById<TextView>(R.id.freq6),
            findViewById<TextView>(R.id.freq7)
        )

        freqList.forEach { freq ->
            freq.setOnClickListener {
                freqList.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#0F172A"))
                }
                freq.setBackgroundResource(R.drawable.bg_chip_selected)
                freq.setTextColor(Color.WHITE)
                selectedFreq = freq
            }
        }

        if (isModoEdicao && userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val triagem = doc.get("triagem") as? Map<*, *>

                        TriagemDataManager.experiencia = (triagem?.get("experiencia") as? String)
                            ?: doc.getString("experiencia") ?: ""
                        TriagemDataManager.objetivo = (triagem?.get("objetivo") as? String)
                            ?: doc.getString("objetivo") ?: ""
                        TriagemDataManager.frequencia = (triagem?.get("frequencia") as? String)
                            ?: doc.getString("frequencia") ?: doc.getString("frequenciaIdeal") ?: ""

                        val pesoVal = (triagem?.get("peso"))?.toString()
                            ?: doc.get("peso")?.toString() ?: ""
                        TriagemDataManager.peso = pesoVal

                        val alturaVal = (triagem?.get("altura"))?.toString()
                            ?: doc.get("altura")?.toString() ?: ""
                        TriagemDataManager.altura = alturaVal

                        val imcVal = (triagem?.get("imc"))?.toString()
                            ?: doc.get("imc")?.toString() ?: ""
                        TriagemDataManager.imc = imcVal

                        val statusParqVal = (triagem?.get("statusParq") as? String)
                            ?: doc.getString("statusParq") ?: doc.getString("parqStatus") ?: "Sem restrições"
                        TriagemDataManager.statusParq = statusParqVal

                        TriagemDataManager.swQ1 = statusParqVal.contains("cardíaco", ignoreCase = true)
                        TriagemDataManager.swQ2 = statusParqVal.contains("torácica", ignoreCase = true) || statusParqVal.contains("peito", ignoreCase = true)
                        TriagemDataManager.swQ3 = statusParqVal.contains("tontura", ignoreCase = true) || statusParqVal.contains("desmaio", ignoreCase = true)
                        TriagemDataManager.swQ4 = statusParqVal.contains("osteoarticular", ignoreCase = true)
                        TriagemDataManager.swQ5 = statusParqVal.contains("hipertensivo", ignoreCase = true) || statusParqVal.contains("pressão", ignoreCase = true)

                        TriagemDataManager.lesoes = (triagem?.get("lesoes") as? String)
                            ?: doc.getString("lesoes") ?: doc.getString("lesao") ?: ""
                        TriagemDataManager.esportes = doc.getString("esportes") ?: ""
                        TriagemDataManager.observacoes = doc.getString("observacoes") ?: ""

                        preencherCamposEdicao(expList, objList, etOutro, freqList)
                    }
                }
        }

        findViewById<Button>(R.id.btnProsseguirTriagem1)?.setOnClickListener {
            val exp = selectedExp?.text?.toString() ?: ""
            if (exp.isEmpty()) {
                Toast.makeText(this, "Selecione seu nível de experiência.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var obj = selectedObj?.text?.toString() ?: ""
            if (obj.isEmpty()) {
                Toast.makeText(this, "Selecione seu objetivo principal.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedObj == chipObj6) {
                val outroTexto = etOutro.text.toString().trim()
                if (outroTexto.isEmpty()) {
                    Toast.makeText(this, "Digite seu objetivo no campo informado.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                obj = outroTexto
            }

            val freqRaw = selectedFreq?.text?.toString() ?: ""
            if (freqRaw.isEmpty()) {
                Toast.makeText(this, "Selecione a frequência semanal ideal.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val freqFormatted = if (freqRaw.endsWith("/ semana", ignoreCase = true)) freqRaw else "$freqRaw / semana"

            TriagemDataManager.experiencia = exp
            TriagemDataManager.objetivo = obj
            TriagemDataManager.frequencia = freqFormatted

            val nextIntent = Intent(this, TriagemEtapa2Activity::class.java)
            nextIntent.putExtra("USER_UID", userId)
            nextIntent.putExtra("user_id", userId)
            nextIntent.putExtra("MODO_EDICAO", isModoEdicao)
            startActivity(nextIntent)
        }
    }

    private fun preencherCamposEdicao(
        expList: List<TextView>,
        objList: List<TextView>,
        etOutro: EditText,
        freqList: List<TextView>
    ) {
        // 1. Experiência
        val exp = TriagemDataManager.experiencia
        if (exp.isNotEmpty()) {
            val matchedExp = expList.firstOrNull { chip ->
                chip.text.toString().contains(exp, ignoreCase = true) ||
                exp.contains(chip.text.toString(), ignoreCase = true) ||
                (exp.contains("iniciante", ignoreCase = true) && chip.id == R.id.chipExp1) ||
                (exp.contains("parado", ignoreCase = true) && chip.id == R.id.chipExp2) ||
                (exp.contains("regularmente", ignoreCase = true) && chip.id == R.id.chipExp3)
            }
            if (matchedExp != null) {
                expList.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#0F172A"))
                }
                matchedExp.setBackgroundResource(R.drawable.bg_chip_selected)
                matchedExp.setTextColor(Color.WHITE)
                selectedExp = matchedExp
            }
        }

        // 2. Objetivo
        val obj = TriagemDataManager.objetivo
        if (obj.isNotEmpty()) {
            val matchedObj = objList.dropLast(1).firstOrNull { chip ->
                chip.text.toString().equals(obj, ignoreCase = true)
            }
            objList.forEach {
                it.setBackgroundResource(R.drawable.bg_chip_unselected)
                it.setTextColor(Color.parseColor("#0F172A"))
            }

            if (matchedObj != null) {
                matchedObj.setBackgroundResource(R.drawable.bg_chip_selected)
                matchedObj.setTextColor(Color.WHITE)
                selectedObj = matchedObj
                etOutro.visibility = View.GONE
            } else {
                val chipOutro = objList.last()
                chipOutro.setBackgroundResource(R.drawable.bg_chip_selected)
                chipOutro.setTextColor(Color.WHITE)
                selectedObj = chipOutro
                etOutro.visibility = View.VISIBLE
                etOutro.setText(obj)
            }
        }

        // 3. Frequência
        val freq = TriagemDataManager.frequencia
        if (freq.isNotEmpty()) {
            val matchedFreq = freqList.firstOrNull { chip ->
                freq.startsWith(chip.text.toString(), ignoreCase = true)
            }
            if (matchedFreq != null) {
                freqList.forEach {
                    it.setBackgroundResource(R.drawable.bg_chip_unselected)
                    it.setTextColor(Color.parseColor("#0F172A"))
                }
                matchedFreq.setBackgroundResource(R.drawable.bg_chip_selected)
                matchedFreq.setTextColor(Color.WHITE)
                selectedFreq = matchedFreq
            }
        }
    }
}
