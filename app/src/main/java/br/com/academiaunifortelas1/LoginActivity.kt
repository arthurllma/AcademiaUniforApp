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
import com.google.firebase.firestore.FirebaseFirestore

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

            // Login fixo para o Administrador
            if (email == "adm@unifor.br" && senha == "Unifor*123") {
                getSharedPreferences("UsuarioPref", MODE_PRIVATE).edit()
                    .putString("user_role", "admin")
                    .putString("nome_completo", "Administrador")
                    .putString("email", "adm@unifor.br")
                    .apply()

                Toast.makeText(this, "Login de Administrador realizado com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeInstrutorActivity::class.java)
                intent.putExtra("IS_ADMIN", true)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            // Rota especial mockada para instrutor
            if (email == "instrutor@unifor.br" && senha == "Unifor*123") {
                getSharedPreferences("UsuarioPref", MODE_PRIVATE).edit()
                    .putString("user_role", "instrutor")
                    .putString("nome_completo", "Instrutor")
                    .putString("email", "instrutor@unifor.br")
                    .apply()

                val intent = Intent(this, HomeInstrutorActivity::class.java)
                intent.putExtra("IS_ADMIN", false)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    val userId = auth.currentUser?.uid ?: ""
                    if (userId.isNotEmpty()) {
                        FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                btnLogin.isEnabled = true

                                if (document != null && document.exists()) {
                                    val tipo = document.getString("tipo")?.trim()?.lowercase() ?: "aluno"
                                    val isTriagemConcluida = document.getBoolean("isTriagemConcluida")
                                        ?: document.getBoolean("triagemConcluida")
                                        ?: false

                                    // 1. Se aluno e triagem não foi concluída -> redirecionar para TriagemActivity
                                    if (tipo == "aluno" && !isTriagemConcluida) {
                                        val intent = Intent(this, TriagemActivity::class.java)
                                        intent.putExtra("USER_UID", userId)
                                        intent.putExtra("user_id", userId)
                                        startActivity(intent)
                                        finish()
                                        return@addOnSuccessListener
                                    }

                                    val status = document.getString("status")?.trim()?.lowercase() ?: "aprovado"

                                    // 2. Se status == "pendente" -> bloquear
                                    if (status == "pendente") {
                                        auth.signOut()
                                        val msg = "Seu cadastro ainda está aguardando aprovação do Administrador."
                                        tvErrorSenha?.text = msg
                                        tvErrorSenha?.visibility = View.VISIBLE
                                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                                        return@addOnSuccessListener
                                    }

                                    // 3. Se status == "rejeitado" -> bloquear
                                    if (status == "rejeitado") {
                                        auth.signOut()
                                        val msg = "Seu cadastro foi rejeitado pela administração."
                                        tvErrorSenha?.text = msg
                                        tvErrorSenha?.visibility = View.VISIBLE
                                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                                        return@addOnSuccessListener
                                    }

                                    // 4. Se status == "aprovado" -> autorizar login
                                    val nomeCompleto = document.getString("nome") ?: ""
                                    val emailUser = document.getString("email") ?: email
                                    val primeiroNome = nomeCompleto.trim().split(" ").firstOrNull() ?: ""
                                    val userRole = if (tipo == "adm") "admin" else if (tipo == "instrutor") "instrutor" else "aluno"

                                    getSharedPreferences("UsuarioPref", MODE_PRIVATE).edit()
                                        .putString("user_id", userId)
                                        .putString("user_role", userRole)
                                        .putString("nome_completo", nomeCompleto)
                                        .putString("primeiro_nome", primeiroNome)
                                        .putString("email", emailUser)
                                        .apply()

                                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                                    when (tipo) {
                                        "adm" -> {
                                            val intent = Intent(this, HomeInstrutorActivity::class.java)
                                            intent.putExtra("IS_ADMIN", true)
                                            startActivity(intent)
                                            finish()
                                        }
                                        "instrutor" -> {
                                            val intent = Intent(this, HomeInstrutorActivity::class.java)
                                            intent.putExtra("IS_ADMIN", false)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else -> {
                                            startActivity(Intent(this, HomeAlunoActivity::class.java))
                                            finish()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, HomeAlunoActivity::class.java))
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                btnLogin.isEnabled = true
                                tvErrorSenha?.text = "Erro ao carregar perfil do usuário."
                                tvErrorSenha?.visibility = View.VISIBLE
                            }
                    } else {
                        btnLogin.isEnabled = true
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeAlunoActivity::class.java))
                        finish()
                    }
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
