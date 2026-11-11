package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PerfilBiometriaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_biometria)
        
        carregarDadosAnamnese()

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
                    finish() // Just close and return to PerfilAlunoActivity
                    true
                }
                else -> false
            }
        }
    }

    private fun carregarDadosAnamnese() {
        val sharedPref = getSharedPreferences("TriagemDados", MODE_PRIVATE)
        
        val objetivo = sharedPref.getString("objetivo", "Hipertrofia/Ganho de massa")
        val experiencia = sharedPref.getString("experiencia", "Iniciante")
        val restricoes = sharedPref.getString("restricoes", "Sensibilidade no joelho direito, Hérnia de disco (L4/L5).")
        val parqAlertas = sharedPref.getString("parqAlertas", "Hipertensão arterial (sob medicação).")

        findViewById<TextView>(R.id.tvObjetivo).text = objetivo
        findViewById<TextView>(R.id.tvExperiencia).text = experiencia
        findViewById<TextView>(R.id.tvRestricoes).text = restricoes
        findViewById<TextView>(R.id.tvParq).text = parqAlertas
    }
}
