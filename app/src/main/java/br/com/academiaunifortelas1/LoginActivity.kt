package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            val paddingPx = (24 * resources.displayMetrics.density).toInt()
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(paddingPx, systemBars.top, paddingPx, systemBars.bottom)
                insets
            }
        }

        auth = FirebaseAuth.getInstance()

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val tvErrorEmail = findViewById<TextView>(R.id.tvErrorEmail)
        val tvErrorSenha = findViewById<TextView>(R.id.tvErrorSenha)
        val btnLogin = findViewById<Button>(R.id.btnFazerLogin)

        btnLogin?.setOnClickListener {
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

            // Rota especial mockada para instrutor
            if (email == "instrutor@unifor.br" && senha == "Unifor*123") {
                startActivity(Intent(this, HomeInstrutorActivity::class.java))
                finish()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            // Autenticar com o Firebase Authentication
            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeAlunoActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    tvErrorSenha?.text = "Email ou senha incorretos."
                    tvErrorSenha?.visibility = View.VISIBLE
                    btnLogin.isEnabled = true
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