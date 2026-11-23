package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_aluno)

        carregarSaudacaoUsuario()

        val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipperAluno)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> viewFlipper.displayedChild = 0
                R.id.navTreinos -> viewFlipper.displayedChild = 1
                R.id.navMapa -> {
                    startActivity(Intent(this, MapaActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                }
                R.id.navPerfil -> {
                    startActivity(Intent(this, PerfilAlunoActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        // Click no botao de Ver Ficha Completa na aba Home -> Leva pra aba de Treino
        val goToTreinoListener = View.OnClickListener {
            bottomNav.selectedItemId = R.id.navTreinos
        }
        
        findViewById<View>(R.id.btnVerFichaTreino1)?.setOnClickListener(goToTreinoListener)
        findViewById<View>(R.id.btnVerFichaTreino2)?.setOnClickListener(goToTreinoListener)

        // Botao de Iniciar Treino
        findViewById<Button>(R.id.btnIniciarTreino)?.setOnClickListener {
            startActivity(Intent(this, IniciarTreinoActivity::class.java))
        }

        handleIntent(intent)
    }

    private fun carregarSaudacaoUsuario() {
        val tvSaudacao = findViewById<TextView>(R.id.tvSaudacaoAluno) ?: return

        val prefs = getSharedPreferences("UsuarioPref", MODE_PRIVATE)
        val primeiroNomeLocal = prefs.getString("primeiro_nome", "") ?: ""

        if (primeiroNomeLocal.isNotEmpty()) {
            tvSaudacao.text = "Olá, $primeiroNomeLocal!"
        }

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeCompleto = document.getString("nome") ?: ""
                        if (nomeCompleto.isNotEmpty()) {
                            val pNome = nomeCompleto.trim().split(" ").firstOrNull() ?: ""
                            if (pNome.isNotEmpty()) {
                                tvSaudacao.text = "Olá, $pNome!"
                                prefs.edit().putString("primeiro_nome", pNome).putString("nome_completo", nomeCompleto).apply()
                            }
                        }
                    }
                }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val tabId = intent.getIntExtra("selected_tab", -1)
        if (tabId != -1) {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.selectedItemId = tabId
        }
    }

    override fun onResume() {
        super.onResume()
        carregarSaudacaoUsuario()
        // Garante que a barra selecione o item correto dependendo da view mostrada
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipperAluno)
        when (viewFlipper.displayedChild) {
            0 -> if (bottomNav.selectedItemId != R.id.navHome) bottomNav.selectedItemId = R.id.navHome
            1 -> if (bottomNav.selectedItemId != R.id.navTreinos) bottomNav.selectedItemId = R.id.navTreinos
        }
    }
}
