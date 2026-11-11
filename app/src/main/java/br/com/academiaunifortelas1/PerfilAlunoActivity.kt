package br.com.academiaunifortelas1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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

    private fun carregarDadosAluno() {
        val sharedPref = getSharedPreferences("TriagemDados", MODE_PRIVATE)
        val peso = sharedPref.getString("peso", "78")
        val altura = sharedPref.getString("altura", "175")
        val imc = sharedPref.getString("imc", "25.5")

        findViewById<TextView>(R.id.tvPeso).text = "$peso kg"
        findViewById<TextView>(R.id.tvAltura).text = "$altura cm"
        findViewById<TextView>(R.id.tvImc).text = imc
    }
}
