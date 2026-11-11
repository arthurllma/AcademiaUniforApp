package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TriagemEtapa3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa3)

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
            startActivity(Intent(this, TriagemEtapa4Activity::class.java))
        }
    }
}
