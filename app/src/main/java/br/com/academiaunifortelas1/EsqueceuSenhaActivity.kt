package br.com.academiaunifortelas1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class EsqueceuSenhaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_esqueceu_senha)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener {
            finish()
        }

        val etEmail = findViewById<EditText>(R.id.etEmailRecuperacao)
        findViewById<Button>(R.id.btnEnviarLink)?.setOnClickListener {
            val emailText = etEmail?.text?.toString() ?: ""
            Toast.makeText(this, "Link enviado para: $emailText", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnVoltarLogin)?.setOnClickListener {
            finish()
        }
    }
}
