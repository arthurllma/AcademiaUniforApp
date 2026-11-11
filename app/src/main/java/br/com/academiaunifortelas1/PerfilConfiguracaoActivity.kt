package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilConfiguracaoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_configuracao)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }
        
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
                R.id.navPerfil -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.btnAlterarFoto).setOnClickListener {
            Toast.makeText(this, "Funcionalidade de alterar foto em breve", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnBaixarPdfContrato).setOnClickListener {
            Toast.makeText(this, "Baixando contrato...", Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.rlAlterarSenha).setOnClickListener {
            Toast.makeText(this, "Redirecionando para alterar senha...", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnSalvar).setOnClickListener {
            Toast.makeText(this, "Alterações salvas!", Toast.LENGTH_SHORT).show()
            finish()
        }

        findViewById<TextView>(R.id.tvCancelar).setOnClickListener {
            finish()
        }
    }
}
