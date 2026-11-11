package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlunosInstrutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alunos_instrutor)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navAlunos

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
                R.id.navMontarFicha -> {
                    startActivity(Intent(this, BibliotecaTreinosInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.navAlunos -> true
                R.id.navPerfilInstrutor -> {
                    startActivity(Intent(this, PerfilInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Mock clicks for Alunos items to open MontarFichaAlunoActivity
        val onAlunoClickListener = View.OnClickListener {
            startActivity(Intent(this, MontarFichaAlunoActivity::class.java))
        }

        val item1 = findViewById<View>(R.id.itemAluno1)
        item1.setOnClickListener(onAlunoClickListener)
        item1.findViewById<TextView>(R.id.tvNomeAluno).text = "João Silva"
        item1.findViewById<TextView>(R.id.tvStatusFicha).text = "Ficha Ativa"
        item1.findViewById<TextView>(R.id.tvStatusFicha).setBackgroundResource(R.drawable.bg_badge_green)
        item1.findViewById<TextView>(R.id.tvStatusFicha).setTextColor(Color.parseColor("#16A34A"))

        val item2 = findViewById<View>(R.id.itemAluno2)
        item2.setOnClickListener(onAlunoClickListener)
        item2.findViewById<TextView>(R.id.tvNomeAluno).text = "Maria Santos"
        item2.findViewById<TextView>(R.id.tvCpfAluno).text = "CPF: ***.***.432-11"
        item2.findViewById<TextView>(R.id.tvStatusFicha).text = "Aguardando Ficha"
        item2.findViewById<TextView>(R.id.tvStatusFicha).setBackgroundResource(R.drawable.bg_badge_yellow)
        item2.findViewById<TextView>(R.id.tvStatusFicha).setTextColor(Color.parseColor("#D97706"))
        
        val item4 = findViewById<View>(R.id.itemAluno4)
        item4.setOnClickListener(onAlunoClickListener)
        item4.findViewById<TextView>(R.id.tvNomeAluno).text = "Ana Costa"
        item4.findViewById<TextView>(R.id.tvCpfAluno).text = "CPF: ***.***.112-99"
        item4.findViewById<TextView>(R.id.tvStatusFicha).text = "Ficha Vencida"
        item4.findViewById<TextView>(R.id.tvStatusFicha).setBackgroundResource(R.drawable.bg_badge_red)
        item4.findViewById<TextView>(R.id.tvStatusFicha).setTextColor(Color.parseColor("#DC2626"))

        // Add listeners for other items if necessary
        findViewById<View>(R.id.itemAluno3).setOnClickListener(onAlunoClickListener)
        findViewById<View>(R.id.itemAluno5).setOnClickListener(onAlunoClickListener)
        findViewById<View>(R.id.itemAluno6).setOnClickListener(onAlunoClickListener)
    }
}
