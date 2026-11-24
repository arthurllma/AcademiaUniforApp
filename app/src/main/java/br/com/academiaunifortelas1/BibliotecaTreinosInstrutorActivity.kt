package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BibliotecaTreinosInstrutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca_treinos_instrutor)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navMontarFicha

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPainel -> {
                    val intent = Intent(this, HomeInstrutorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra("selected_tab", item.itemId)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navMontarFicha -> true
                R.id.navAlunos -> {
                    startActivity(Intent(this, AlunosInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navPerfilInstrutor -> {
                    val role = getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_role", "")
                    val targetClass = if (role == "admin") PerfilAdminActivity::class.java else PerfilInstrutorActivity::class.java
                    startActivity(Intent(this, targetClass))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        findViewById<Button>(R.id.btnMontarNovoTreino).setOnClickListener {
            startActivity(Intent(this, MontarTreinoInstrutorActivity::class.java))
        }

        fun abrirMontarTreino(tipo: String, nome: String) {
            val intent = Intent(this, MontarTreinoInstrutorActivity::class.java)
            intent.putExtra("treino_tipo", tipo)
            intent.putExtra("treino_nome", nome)
            startActivity(intent)
        }

        findViewById<View>(R.id.cardTreinoA)?.setOnClickListener {
            abrirMontarTreino("A", "Hipertrofia • Peito e Tríceps")
        }

        findViewById<View>(R.id.cardTreinoB)?.setOnClickListener {
            abrirMontarTreino("B", "Hipertrofia • Pernas e Glúteos")
        }

        findViewById<View>(R.id.cardTreinoC)?.setOnClickListener {
            abrirMontarTreino("C", "Hipertrofia • Costas e Bíceps")
        }
    }
}
