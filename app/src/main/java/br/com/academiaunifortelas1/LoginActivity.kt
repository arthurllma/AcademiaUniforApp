package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val tvErrorEmail = findViewById<TextView>(R.id.tvErrorEmail)
        val tvErrorSenha = findViewById<TextView>(R.id.tvErrorSenha)

        findViewById<Button>(R.id.btnFazerLogin)?.setOnClickListener {
            val email = etEmail?.text?.toString()?.trim() ?: ""
            val senha = etSenha?.text?.toString() ?: ""

            // Resetar erros visuais
            tvErrorEmail?.visibility = View.GONE
            tvErrorSenha?.visibility = View.GONE

            if (email.isEmpty() || !email.contains("@")) {
                tvErrorEmail?.text = "Insira um email válido."
                tvErrorEmail?.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (senha.isEmpty()) {
                tvErrorSenha?.text = "Insira sua senha."
                tvErrorSenha?.visibility = View.VISIBLE
                return@setOnClickListener
            }

            // Validar credenciais para as rotas
            if (email == "usuario@unifor.br" && senha == "Unifor*123") {
                startActivity(Intent(this, HomeAlunoActivity::class.java))
                finish()
            } else if (email == "instrutor@unifor.br" && senha == "Unifor*123") {
                startActivity(Intent(this, HomeInstrutorActivity::class.java))
                finish()
            } else {
                tvErrorSenha?.text = "Email ou senha incorretos."
                tvErrorSenha?.visibility = View.VISIBLE
            }
        }

        findViewById<View>(R.id.tvEsqueciSenha)?.setOnClickListener {
            startActivity(Intent(this, EsqueceuSenhaActivity::class.java))
        }

        findViewById<View>(R.id.btnIrCadastro)?.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}