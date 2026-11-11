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

class TriagemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem)

        // Experiência Chips
        val chipExp1 = findViewById<TextView>(R.id.chipExp1)
        val chipExp2 = findViewById<TextView>(R.id.chipExp2)
        val chipExp3 = findViewById<TextView>(R.id.chipExp3)
        val expList = listOf(chipExp1, chipExp2, chipExp3)

        expList.forEach { chip ->
            chip.setOnClickListener {
                expList.forEach { it.setBackgroundResource(R.drawable.bg_chip_unselected); it.setTextColor(Color.parseColor("#0F172A")) }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)
            }
        }

        // Objetivo Chips
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
                objList.forEach { it.setBackgroundResource(R.drawable.bg_chip_unselected); it.setTextColor(Color.parseColor("#0F172A")) }
                chip.setBackgroundResource(R.drawable.bg_chip_selected)
                chip.setTextColor(Color.WHITE)

                if (chip == chipObj6) {
                    etOutro.visibility = View.VISIBLE
                } else {
                    etOutro.visibility = View.GONE
                }
            }
        }

        // Frequência Chips
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
                freqList.forEach { it.setBackgroundResource(R.drawable.bg_chip_unselected); it.setTextColor(Color.parseColor("#0F172A")) }
                freq.setBackgroundResource(R.drawable.bg_chip_selected)
                freq.setTextColor(Color.WHITE)
            }
        }

        // Prosseguir
        findViewById<Button>(R.id.btnProsseguirTriagem1)?.setOnClickListener {
            startActivity(Intent(this, TriagemEtapa2Activity::class.java))
        }
    }
}
