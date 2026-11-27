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
import com.google.firebase.firestore.SetOptions

class TriagemEtapa4Activity : AppCompatActivity() {

    private val selectedLesoes = mutableSetOf<TextView>()
    private var isNenhumaLesaoSelected = false
    private var isOutrasLesoesSelected = false

    private val selectedEsportes = mutableSetOf<TextView>()
    private var isNenhumEsporteSelected = false
    private var isOutrosEsportesSelected = false

    private var userId: String = ""
    private var isModoEdicao: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa4)

        isModoEdicao = intent.getBooleanExtra("MODO_EDICAO", false)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

        val btnProsseguir = findViewById<Button>(R.id.btnProsseguirTriagem4)

        if (isModoEdicao) {
            findViewById<TextView>(R.id.tvHeaderTitulo)?.text = "EDITAR ANAMNESE"
            findViewById<TextView>(R.id.tvHeaderEtapa)?.text = "Etapa 4 de 4"
            findViewById<ProgressBar>(R.id.pbTriagem)?.apply {
                max = 4
                progress = 4
            }
            btnProsseguir?.text = "Salvar Anamnese"
        }

        val btnVoltar = findViewById<View>(R.id.btnVoltarTriagem)
        btnVoltar?.setOnClickListener { finish() }

        val lesoesList = listOf(
            findViewById<TextView>(R.id.chipLesao1),
            findViewById<TextView>(R.id.chipLesao2),
            findViewById<TextView>(R.id.chipLesao3),
            findViewById<TextView>(R.id.chipLesao4),
            findViewById<TextView>(R.id.chipLesao5),
            findViewById<TextView>(R.id.chipLesao6)
        )
        val chipLesaoNenhuma = findViewById<TextView>(R.id.chipLesaoNenhuma)
        val chipLesaoOutras = findViewById<TextView>(R.id.chipLesaoOutras)
        val etOutrasLesoes = findViewById<EditText>(R.id.etOutrasLesoes)

        fun updateChipVisual(chip: TextView, isSelected: Boolean) {
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(Color.parseColor("#0F172A"))
            }
        }

        lesoesList.forEach { chip ->
            chip.setOnClickListener {
                isNenhumaLesaoSelected = false
                updateChipVisual(chipLesaoNenhuma, false)

                if (selectedLesoes.contains(chip)) {
                    selectedLesoes.remove(chip)
                    updateChipVisual(chip, false)
                } else {
                    selectedLesoes.add(chip)
                    updateChipVisual(chip, true)
                }
            }
        }

        chipLesaoNenhuma.setOnClickListener {
            selectedLesoes.clear()
            lesoesList.forEach { updateChipVisual(it, false) }
            isOutrasLesoesSelected = false
            updateChipVisual(chipLesaoOutras, false)
            etOutrasLesoes.visibility = View.GONE

            isNenhumaLesaoSelected = true
            updateChipVisual(chipLesaoNenhuma, true)
        }

        chipLesaoOutras.setOnClickListener {
            isNenhumaLesaoSelected = false
            updateChipVisual(chipLesaoNenhuma, false)

            isOutrasLesoesSelected = !isOutrasLesoesSelected
            updateChipVisual(chipLesaoOutras, isOutrasLesoesSelected)
            etOutrasLesoes.visibility = if (isOutrasLesoesSelected) View.VISIBLE else View.GONE
        }

        val esportesList = listOf(
            findViewById<TextView>(R.id.chipEsporte1),
            findViewById<TextView>(R.id.chipEsporte2),
            findViewById<TextView>(R.id.chipEsporte3)
        )
        val chipEsporteNenhum = findViewById<TextView>(R.id.chipEsporte4)
        val chipEsporteOutros = findViewById<TextView>(R.id.chipEsporteOutros)
        val etOutrosEsportes = findViewById<EditText>(R.id.etOutrosEsportes)

        esportesList.forEach { chip ->
            chip.setOnClickListener {
                isNenhumEsporteSelected = false
                updateChipVisual(chipEsporteNenhum, false)

                if (selectedEsportes.contains(chip)) {
                    selectedEsportes.remove(chip)
                    updateChipVisual(chip, false)
                } else {
                    selectedEsportes.add(chip)
                    updateChipVisual(chip, true)
                }
            }
        }

        chipEsporteNenhum.setOnClickListener {
            selectedEsportes.clear()
            esportesList.forEach { updateChipVisual(it, false) }
            isOutrosEsportesSelected = false
            updateChipVisual(chipEsporteOutros, false)
            etOutrosEsportes.visibility = View.GONE

            isNenhumEsporteSelected = true
            updateChipVisual(chipEsporteNenhum, true)
        }

        chipEsporteOutros.setOnClickListener {
            isNenhumEsporteSelected = false
            updateChipVisual(chipEsporteNenhum, false)

            isOutrosEsportesSelected = !isOutrosEsportesSelected
            updateChipVisual(chipEsporteOutros, isOutrosEsportesSelected)
            etOutrosEsportes.visibility = if (isOutrosEsportesSelected) View.VISIBLE else View.GONE
        }

        val etObservacoes = findViewById<EditText>(R.id.etObservacoes)

        if (isModoEdicao) {
            val lesoesStr = TriagemDataManager.lesoes
            if (lesoesStr.equals("Sem lesões", ignoreCase = true) || lesoesStr.equals("Nenhuma", ignoreCase = true) || lesoesStr.equals("Nenhuma Lesão", ignoreCase = true)) {
                isNenhumaLesaoSelected = true
                updateChipVisual(chipLesaoNenhuma, true)
            } else if (lesoesStr.isNotEmpty()) {
                val partes = lesoesStr.split(",").map { it.trim() }
                val outrasTextos = mutableListOf<String>()

                partes.forEach { parte ->
                    val matchedChip = lesoesList.firstOrNull { it.text.toString().equals(parte, ignoreCase = true) }
                    if (matchedChip != null) {
                        selectedLesoes.add(matchedChip)
                        updateChipVisual(matchedChip, true)
                    } else if (parte.isNotBlank()) {
                        outrasTextos.add(parte)
                    }
                }

                if (outrasTextos.isNotEmpty()) {
                    isOutrasLesoesSelected = true
                    updateChipVisual(chipLesaoOutras, true)
                    etOutrasLesoes.visibility = View.VISIBLE
                    etOutrasLesoes.setText(outrasTextos.joinToString(", "))
                }
            }

            val esportesStr = TriagemDataManager.esportes
            if (esportesStr.equals("Nenhum", ignoreCase = true) || esportesStr.equals("Nenhum esporte", ignoreCase = true)) {
                isNenhumEsporteSelected = true
                updateChipVisual(chipEsporteNenhum, true)
            } else if (esportesStr.isNotEmpty()) {
                val partes = esportesStr.split(",").map { it.trim() }
                val outrosTextos = mutableListOf<String>()

                partes.forEach { parte ->
                    val matchedChip = esportesList.firstOrNull { it.text.toString().equals(parte, ignoreCase = true) }
                    if (matchedChip != null) {
                        selectedEsportes.add(matchedChip)
                        updateChipVisual(matchedChip, true)
                    } else if (parte.isNotBlank()) {
                        outrosTextos.add(parte)
                    }
                }

                if (outrosTextos.isNotEmpty()) {
                    isOutrosEsportesSelected = true
                    updateChipVisual(chipEsporteOutros, true)
                    etOutrosEsportes.visibility = View.VISIBLE
                    etOutrosEsportes.setText(outrosTextos.joinToString(", "))
                }
            }

            if (TriagemDataManager.observacoes.isNotEmpty()) {
                etObservacoes.setText(TriagemDataManager.observacoes)
            }
        }

        btnProsseguir?.setOnClickListener {
            val temLesaoEspecifica = selectedLesoes.isNotEmpty() || isOutrasLesoesSelected
            if (!isNenhumaLesaoSelected && !temLesaoEspecifica) {
                Toast.makeText(this, "Selecione ao menos uma opção de lesão (ou 'Nenhuma Lesão').", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isOutrasLesoesSelected && etOutrasLesoes.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Descreva as outras lesões ou dores antigas.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val temEsporteEspecifico = selectedEsportes.isNotEmpty() || isOutrosEsportesSelected
            if (!isNenhumEsporteSelected && !temEsporteEspecifico) {
                Toast.makeText(this, "Selecione ao menos uma opção de esporte (ou 'Nenhum').", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isOutrosEsportesSelected && etOutrosEsportes.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Descreva os outros esportes praticados.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val lesoesFormatadas = if (isNenhumaLesaoSelected) {
                "Sem lesões"
            } else {
                val partes = mutableListOf<String>()
                partes.addAll(selectedLesoes.map { it.text.toString() })
                if (isOutrasLesoesSelected) {
                    val textoOutras = etOutrasLesoes.text.toString().trim()
                    if (textoOutras.isNotEmpty()) {
                        partes.add(textoOutras)
                    }
                }
                if (partes.isEmpty()) "Sem lesões" else partes.joinToString(", ")
            }

            val esportesFormatados = if (isNenhumEsporteSelected) {
                "Nenhum"
            } else {
                val partes = mutableListOf<String>()
                partes.addAll(selectedEsportes.map { it.text.toString() })
                if (isOutrosEsportesSelected) {
                    val textoOutras = etOutrosEsportes.text.toString().trim()
                    if (textoOutras.isNotEmpty()) {
                        partes.add(textoOutras)
                    }
                }
                if (partes.isEmpty()) "Nenhum" else partes.joinToString(", ")
            }

            TriagemDataManager.lesoes = lesoesFormatadas
            TriagemDataManager.esportes = esportesFormatados
            TriagemDataManager.observacoes = etObservacoes.text.toString().trim()

            if (isModoEdicao) {
                val db = FirebaseFirestore.getInstance()

                val pesoDouble = TriagemDataManager.peso.toDoubleOrNull() ?: 0.0
                val alturaDouble = TriagemDataManager.altura.toDoubleOrNull() ?: 0.0
                val imcDouble = TriagemDataManager.imc.replace(",", ".").toDoubleOrNull() ?: 0.0

                val triagemMap = hashMapOf<String, Any>(
                    "experiencia" to TriagemDataManager.experiencia,
                    "objetivo" to TriagemDataManager.objetivo,
                    "frequencia" to TriagemDataManager.frequencia,
                    "peso" to pesoDouble,
                    "altura" to alturaDouble,
                    "imc" to imcDouble,
                    "statusParq" to TriagemDataManager.statusParq,
                    "lesoes" to TriagemDataManager.lesoes
                )

                val updates = hashMapOf<String, Any>(
                    "objetivo" to TriagemDataManager.objetivo,
                    "frequencia" to TriagemDataManager.frequencia,
                    "frequenciaIdeal" to TriagemDataManager.frequencia,
                    "experiencia" to TriagemDataManager.experiencia,
                    "peso" to TriagemDataManager.peso,
                    "altura" to TriagemDataManager.altura,
                    "statusParq" to TriagemDataManager.statusParq,
                    "parqStatus" to TriagemDataManager.statusParq,
                    "parqRestricoes" to TriagemDataManager.parqRestricoes,
                    "lesoes" to TriagemDataManager.lesoes,
                    "lesao" to TriagemDataManager.lesoes,
                    "esportes" to TriagemDataManager.esportes,
                    "observacoes" to TriagemDataManager.observacoes,
                    "triagem" to triagemMap
                )

                getSharedPreferences("TriagemDados", MODE_PRIVATE).edit()
                    .putString("experiencia", TriagemDataManager.experiencia)
                    .putString("objetivo", TriagemDataManager.objetivo)
                    .putString("frequencia", TriagemDataManager.frequencia)
                    .putString("peso", TriagemDataManager.peso)
                    .putString("altura", TriagemDataManager.altura)
                    .putString("imc", TriagemDataManager.imc)
                    .putString("statusParq", TriagemDataManager.statusParq)
                    .putBoolean("parqRestricoes", TriagemDataManager.parqRestricoes)
                    .putString("lesoes", TriagemDataManager.lesoes)
                    .putString("esportes", TriagemDataManager.esportes)
                    .putString("observacoes", TriagemDataManager.observacoes)
                    .apply()

                btnProsseguir.isEnabled = false

                db.collection("usuarios").document(userId)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Anamnese atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, PerfilBiometriaActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        btnProsseguir.isEnabled = true
                        Toast.makeText(this, "Erro ao atualizar anamnese: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                val nextIntent = Intent(this, TriagemEtapa5Activity::class.java)
                nextIntent.putExtra("USER_UID", userId)
                nextIntent.putExtra("user_id", userId)
                startActivity(nextIntent)
            }
        }
    }
}
