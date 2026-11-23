package br.com.academiaunifortelas1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_aluno)
        
        carregarDadosAluno()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.navPerfil

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome, R.id.navTreinos, R.id.navMapa -> {
                    val intent = Intent(this, HomeAlunoActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra("selected_tab", item.itemId)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navPerfil -> true
                else -> false
            }
        }

        findViewById<ImageView>(R.id.ivSettings).setOnClickListener {
            startActivity(Intent(this, PerfilConfiguracaoActivity::class.java))
        }

        findViewById<Button>(R.id.btnVerDetalhes).setOnClickListener {
            startActivity(Intent(this, PerfilBiometriaActivity::class.java))
        }

        findViewById<View>(R.id.rlAulasDisponiveis).setOnClickListener {
            startActivity(Intent(this, PerfilAulasActivity::class.java))
        }

        findViewById<Button>(R.id.btnCopiarPix).setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("PIX", "00020126580014br.gov.bcb.pix0136123e4567-e89b-12d3-a456-426655440000520400005303986540510.005802BR5915Academia Unifor6009Fortaleza62070503***6304A1B2")
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "PIX copiado com sucesso!", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnBaixarPdf).setOnClickListener {
            Toast.makeText(this, "Baixando boleto da mensalidade...", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnSair).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        carregarDadosAluno()
    }

    private fun carregarDadosAluno() {
        val sharedPref = getSharedPreferences("TriagemDados", MODE_PRIVATE)
        val peso = sharedPref.getString("peso", "78")
        val altura = sharedPref.getString("altura", "175")
        val imc = sharedPref.getString("imc", "25.5")

        findViewById<TextView>(R.id.tvPeso)?.text = "$peso kg"
        findViewById<TextView>(R.id.tvAltura)?.text = "$altura cm"
        findViewById<TextView>(R.id.tvImc)?.text = imc

        val tvNome = findViewById<TextView>(R.id.tvNome)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)

        // 1. Tentar carregar primeiro das preferências locais
        val userPrefs = getSharedPreferences("UsuarioPref", MODE_PRIVATE)
        val nomeLocal = userPrefs.getString("nome_completo", "") ?: ""
        val emailLocal = userPrefs.getString("email", "") ?: ""

        if (nomeLocal.isNotEmpty() && tvNome != null) tvNome.text = nomeLocal
        if (emailLocal.isNotEmpty() && tvEmail != null) tvEmail.text = emailLocal

        // 2. Buscar diretamente do Firebase Firestore para o usuário logado
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: ""

        if (userId.isNotEmpty()) {
            // Se o e-mail do Auth estiver disponível, atualiza imediatamente
            currentUser?.email?.let {
                if (it.isNotEmpty() && tvEmail != null) tvEmail.text = it
            }

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeCompleto = document.getString("nome") ?: ""
                        val emailUser = document.getString("email") ?: (currentUser?.email ?: "")

                        if (nomeCompleto.isNotEmpty() && tvNome != null) {
                            tvNome.text = nomeCompleto
                        }
                        if (emailUser.isNotEmpty() && tvEmail != null) {
                            tvEmail.text = emailUser
                        }

                        userPrefs.edit()
                            .putString("nome_completo", nomeCompleto)
                            .putString("email", emailUser)
                            .apply()
                    }
                }
        }
    }
}
