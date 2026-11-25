package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.TriagemDataManager
import com.google.firebase.auth.FirebaseAuth

class TriagemEtapa3Activity : AppCompatActivity() {

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa3)

        userId = intent.getStringExtra("USER_UID")
            ?: intent.getStringExtra("user_id")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

        val btnVoltar = findViewById<View>(R.id.btnVoltarTriagem)
        btnVoltar?.setOnClickListener { finish() }

        val swQ1 = findViewById<Switch>(R.id.swQ1)
        val swQ2 = findViewById<Switch>(R.id.swQ2)
        val swQ3 = findViewById<Switch>(R.id.swQ3)
        val swQ4 = findViewById<Switch>(R.id.swQ4)
        val swQ5 = findViewById<Switch>(R.id.swQ5)

        val tvAlertQ1 = findViewById<TextView>(R.id.tvAlertQ1)
        val tvAlertQ2 = findViewById<TextView>(R.id.tvAlertQ2)
        val tvAlertQ3 = findViewById<TextView>(R.id.tvAlertQ3)
        val tvAlertQ4 = findViewById<TextView>(R.id.tvAlertQ4)
        val tvAlertQ5 = findViewById<TextView>(R.id.tvAlertQ5)

        fun bindSwitch(sw: Switch, alert: TextView) {
            sw.setOnCheckedChangeListener { _, isChecked ->
                alert.visibility = if (isChecked) View.VISIBLE else View.GONE
            }
        }

        bindSwitch(swQ1, tvAlertQ1)
        bindSwitch(swQ2, tvAlertQ2)
        bindSwitch(swQ3, tvAlertQ3)
        bindSwitch(swQ4, tvAlertQ4)
        bindSwitch(swQ5, tvAlertQ5)

        findViewById<Button>(R.id.btnProsseguirTriagem3)?.setOnClickListener {
            val listAlerts = mutableListOf<String>()
            if (swQ1.isChecked) listAlerts.add("Problema cardíaco")
            if (swQ2.isChecked) listAlerts.add("Dor torácica")
            if (swQ3.isChecked) listAlerts.add("Tontura / desmaio")
            if (swQ4.isChecked) listAlerts.add("Lesão osteoarticular")
            if (swQ5.isChecked) listAlerts.add("Uso anti-hipertensivo")

            val hasRestrictions = listAlerts.isNotEmpty()
            val statusParqText = if (hasRestrictions) listAlerts.joinToString(", ") else "Sem restrições"

            TriagemDataManager.swQ1 = swQ1.isChecked
            TriagemDataManager.swQ2 = swQ2.isChecked
            TriagemDataManager.swQ3 = swQ3.isChecked
            TriagemDataManager.swQ4 = swQ4.isChecked
            TriagemDataManager.swQ5 = swQ5.isChecked
            TriagemDataManager.parqRestricoes = hasRestrictions
            TriagemDataManager.statusParq = statusParqText

            val nextIntent = Intent(this, TriagemEtapa4Activity::class.java)
            nextIntent.putExtra("USER_UID", userId)
            nextIntent.putExtra("user_id", userId)
            startActivity(nextIntent)
        }
    }
}
