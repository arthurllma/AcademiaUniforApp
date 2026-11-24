package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.AlunosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeInstrutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_instrutor)

        atualizarSaudacao()
        atualizarCountSemFicha()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navPainel

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPainel -> true
                R.id.navMontarFicha -> {
                    startActivity(Intent(this, BibliotecaTreinosInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navAlunos -> {
                    startActivity(Intent(this, AlunosInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navPerfilInstrutor -> {
                    val role = getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_role", "")
                    val targetClass = if (role == "admin") PerfilAdminActivity::class.java else PerfilInstrutorActivity::class.java
                    startActivity(Intent(this, targetClass))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        val abrirSemFichaListener = View.OnClickListener {
            val intent = Intent(this, AlunosInstrutorActivity::class.java)
            intent.putExtra("filtro_status", "sem_ficha")
            startActivity(intent)
        }

        findViewById<View>(R.id.cardSemFicha)?.setOnClickListener(abrirSemFichaListener)
        findViewById<View>(R.id.btnMontarFichaCard)?.setOnClickListener(abrirSemFichaListener)
        
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val tabId = intent.getIntExtra("selected_tab", -1)
        if (tabId != -1) {
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
            bottomNav.selectedItemId = tabId
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarSaudacao()
        atualizarCountSemFicha()
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        if (bottomNav.selectedItemId != R.id.navPainel) {
             bottomNav.selectedItemId = R.id.navPainel
        }
    }

    private fun atualizarSaudacao() {
        val prefs = getSharedPreferences("UsuarioPref", MODE_PRIVATE)
        val role = prefs.getString("user_role", "")
        val nome = prefs.getString("nome_completo", "")
        val tvSaudacao = findViewById<TextView>(R.id.tvSaudacaoNome)
        if (tvSaudacao != null) {
            if (role == "admin" || nome.equals("Administrador", ignoreCase = true)) {
                tvSaudacao.text = "Olá, Administrador!"
            } else {
                tvSaudacao.text = "Olá, Prof. Yuri!"
            }
        }
    }

    private fun atualizarCountSemFicha() {
        val count = AlunosRepository.getQuantidadeSemFicha()
        findViewById<TextView>(R.id.tvCountSemFicha)?.text = count.toString()
    }
}
