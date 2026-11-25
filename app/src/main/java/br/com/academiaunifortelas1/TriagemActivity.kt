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

class TriagemActivity : AppCompatActivity() {

    private var selectedExp: TextView? = null
    private var selectedObj: TextView? = null
    private var selectedFreq: TextView? = null
    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

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
            startActivity(nextIntent)
        }
    }
}
