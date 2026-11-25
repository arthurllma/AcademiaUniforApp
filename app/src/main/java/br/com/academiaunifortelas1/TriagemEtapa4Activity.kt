package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.TriagemDataManager
import com.google.firebase.auth.FirebaseAuth

class TriagemEtapa4Activity : AppCompatActivity() {

    private val selectedLesoes = mutableSetOf<TextView>()
    private var isNenhumaLesaoSelected = false
    private var isOutrasLesoesSelected = false

    private val selectedEsportes = mutableSetOf<TextView>()
    private var isNenhumEsporteSelected = false
    private var isOutrosEsportesSelected = false

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa4)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

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

        findViewById<Button>(R.id.btnProsseguirTriagem4)?.setOnClickListener {
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

            val nextIntent = Intent(this, TriagemEtapa5Activity::class.java)
            nextIntent.putExtra("USER_UID", userId)
            nextIntent.putExtra("user_id", userId)
            startActivity(nextIntent)
        }
    }
}
