package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilBiometriaActivity : AppCompatActivity() {

    private var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_biometria)
        
        userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

        carregarDadosAnamnese()

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnEditar)?.setOnClickListener {
            val intent = Intent(this, TriagemActivity::class.java)
            intent.putExtra("MODO_EDICAO", true)
            intent.putExtra("USER_UID", userId)
            intent.putExtra("user_id", userId)
            startActivity(intent)
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

    override fun onResume() {
        super.onResume()
        carregarDadosAnamnese()
    }

    private fun carregarDadosAnamnese() {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val triagem = document.get("triagem") as? Map<*, *>

                        val objetivo = triagem?.get("objetivo") as? String
                            ?: document.getString("objetivo")
                            ?: "Hipertrofia/Ganho de massa"

                        val experiencia = triagem?.get("experiencia") as? String
                            ?: document.getString("experiencia")
                            ?: "Iniciante"

                        val restricoes = triagem?.get("lesoes") as? String
                            ?: document.getString("lesoes")
                            ?: document.getString("lesao")
                            ?: "Nenhuma restrição registrada"

                        val statusParq = triagem?.get("statusParq") as? String
                            ?: document.getString("statusParq")
                            ?: document.getString("parqStatus")
                            ?: "Sem restrições"

                        val esportesParalelosRaw = triagem?.get("esportesParalelos") as? String
                            ?: triagem?.get("esportes") as? String
                            ?: document.getString("esportesParalelos")
                            ?: document.getString("esportes")
                        val esportesParalelos = if (esportesParalelosRaw.isNullOrBlank()) "Nenhum" else esportesParalelosRaw

                        val observacoesRaw = triagem?.get("observacoes") as? String
                            ?: document.getString("observacoes")
                        val observacoes = if (observacoesRaw.isNullOrBlank()) "Nenhuma" else observacoesRaw

                        findViewById<TextView>(R.id.tvObjetivo)?.text = objetivo
                        findViewById<TextView>(R.id.tvExperiencia)?.text = experiencia
                        findViewById<TextView>(R.id.tvRestricoes)?.text = restricoes
                        findViewById<TextView>(R.id.tvParq)?.text = statusParq
                        findViewById<TextView>(R.id.tvValorEsportesParalelos)?.text = esportesParalelos
                        findViewById<TextView>(R.id.tvValorObservacoes)?.text = observacoes

                        getSharedPreferences("TriagemDados", MODE_PRIVATE).edit()
                            .putString("objetivo", objetivo)
                            .putString("experiencia", experiencia)
                            .putString("restricoes", restricoes)
                            .putString("parqAlertas", statusParq)
                            .putString("esportesParalelos", esportesParalelos)
                            .putString("observacoes", observacoes)
                            .apply()
                        return@addOnSuccessListener
                    }
                    carregarDadosPrefLocal()
                }
                .addOnFailureListener {
                    carregarDadosPrefLocal()
                }
        } else {
            carregarDadosPrefLocal()
        }
    }

    private fun carregarDadosPrefLocal() {
        val sharedPref = getSharedPreferences("TriagemDados", MODE_PRIVATE)

        val objetivo = sharedPref.getString("objetivo", "Hipertrofia/Ganho de massa")
        val experiencia = sharedPref.getString("experiencia", "Iniciante")
        val restricoes = sharedPref.getString("restricoes", "Nenhuma restrição registrada")
        val parqAlertas = sharedPref.getString("parqAlertas", "Sem restrições")
        val esportesParalelos = sharedPref.getString("esportesParalelos", "Nenhum")
        val observacoes = sharedPref.getString("observacoes", "Nenhuma")

        findViewById<TextView>(R.id.tvObjetivo)?.text = objetivo
        findViewById<TextView>(R.id.tvExperiencia)?.text = experiencia
        findViewById<TextView>(R.id.tvRestricoes)?.text = restricoes
        findViewById<TextView>(R.id.tvParq)?.text = parqAlertas
        findViewById<TextView>(R.id.tvValorEsportesParalelos)?.text = if (esportesParalelos.isNullOrBlank()) "Nenhum" else esportesParalelos
        findViewById<TextView>(R.id.tvValorObservacoes)?.text = if (observacoes.isNullOrBlank()) "Nenhuma" else observacoes
    }
}
