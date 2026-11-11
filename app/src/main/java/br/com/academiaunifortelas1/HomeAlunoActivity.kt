package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_aluno)

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
        // Garante que a barra selecione o item correto dependendo da view mostrada
        // Isso resolve bugs visuais na bottom bar ao voltar da tela do mapa ou perfil usando o botão de "voltar" do celular
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipperAluno)
        when (viewFlipper.displayedChild) {
            0 -> if (bottomNav.selectedItemId != R.id.navHome) bottomNav.selectedItemId = R.id.navHome
            1 -> if (bottomNav.selectedItemId != R.id.navTreinos) bottomNav.selectedItemId = R.id.navTreinos
        }
    }
}
