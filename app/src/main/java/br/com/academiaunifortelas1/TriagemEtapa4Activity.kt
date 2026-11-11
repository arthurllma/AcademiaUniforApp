package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TriagemEtapa4Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa4)

        // Lesões Chips (Múltipla Escolha)
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
        
        // Estado de cada chip (exceto nenhuma/outras que tem logica custom)
        val selectedLesoes = mutableSetOf<TextView>()

        fun updateChipVisual(chip: TextView, isSelected: Boolean) {
            if (isSelected) {
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
            } else {
                chip.setBackgroundResource(R.drawable.bg_chip_unselected)
                chip.setTextColor(Color.parseColor("#0F172A"))
            }
        }

        fun clearOtherLesoes() {
            selectedLesoes.clear()
            lesoesList.forEach { updateChipVisual(it, false) }
            updateChipVisual(chipLesaoOutras, false)
            etOutrasLesoes.visibility = View.GONE
        }

        lesoesList.forEach { chip ->
            chip.setOnClickListener {
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
            clearOtherLesoes()
            updateChipVisual(chipLesaoNenhuma, true)
        }

        chipLesaoOutras.setOnClickListener {
            updateChipVisual(chipLesaoNenhuma, false)
            val isCurrentlySelected = etOutrasLesoes.visibility == View.VISIBLE
            if (isCurrentlySelected) {
                updateChipVisual(chipLesaoOutras, false)
                etOutrasLesoes.visibility = View.GONE
            } else {
                updateChipVisual(chipLesaoOutras, true)
                etOutrasLesoes.visibility = View.VISIBLE
            }
        }


        // Esportes Chips (Múltipla Escolha)
        val esportesList = listOf(
            findViewById<TextView>(R.id.chipEsporte1),
            findViewById<TextView>(R.id.chipEsporte2),
            findViewById<TextView>(R.id.chipEsporte3)
        )
        val chipEsporteNenhum = findViewById<TextView>(R.id.chipEsporte4) // Nenhum
        val chipEsporteOutros = findViewById<TextView>(R.id.chipEsporteOutros)
        val etOutrosEsportes = findViewById<EditText>(R.id.etOutrosEsportes)
        val selectedEsportes = mutableSetOf<TextView>()

        fun clearOtherEsportes() {
            selectedEsportes.clear()
            esportesList.forEach { updateChipVisual(it, false) }
            updateChipVisual(chipEsporteOutros, false)
            etOutrosEsportes.visibility = View.GONE
        }

        esportesList.forEach { chip ->
            chip.setOnClickListener {
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
            clearOtherEsportes()
            updateChipVisual(chipEsporteNenhum, true)
        }

        chipEsporteOutros.setOnClickListener {
            updateChipVisual(chipEsporteNenhum, false)
            val isCurrentlySelected = etOutrosEsportes.visibility == View.VISIBLE
            if (isCurrentlySelected) {
                updateChipVisual(chipEsporteOutros, false)
                etOutrosEsportes.visibility = View.GONE
            } else {
                updateChipVisual(chipEsporteOutros, true)
                etOutrosEsportes.visibility = View.VISIBLE
            }
        }

        findViewById<Button>(R.id.btnProsseguirTriagem4)?.setOnClickListener {
            startActivity(Intent(this, TriagemEtapa5Activity::class.java))
        }
    }
}
