package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class DevMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dev_menu)

        findViewById<View>(R.id.btnFluxoNormal)?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Remove o menu de dev da pilha para o usuário não voltar pra cá acidentalmente
        }
    }
}
