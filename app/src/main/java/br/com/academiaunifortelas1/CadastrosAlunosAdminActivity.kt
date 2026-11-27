package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CadastrosAlunosAdminActivity : AppCompatActivity() {

    private lateinit var llContainerCadastrosPendentes: LinearLayout
    private lateinit var tvTotalPendentes: TextView
    private var listenerFirestore: ListenerRegistration? = null

    private data class AlunoPendenteItem(
        val id: String,
        val nome: String,
        val email: String,
        val genero: String
    )

    private val listaPendentes = mutableListOf<AlunoPendenteItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastros_alunos_admin)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }

        llContainerCadastrosPendentes = findViewById(R.id.llContainerCadastrosPendentes)
        tvTotalPendentes = findViewById(R.id.tvTotalPendentes)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navPerfilInstrutor

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPainel -> {
                    val intent = Intent(this, HomeInstrutorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navMontarFicha -> {
                    startActivity(Intent(this, BibliotecaTreinosInstrutorActivity::class.java))
                    finish()
                    true
                }
                R.id.navAlunos -> {
                    startActivity(Intent(this, AlunosInstrutorActivity::class.java))
                    finish()
                    true
                }
                R.id.navPerfilInstrutor -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        carregarAlunosPendentesFirestore()
    }

    private fun carregarAlunosPendentesFirestore() {
        val db = FirebaseFirestore.getInstance()
        listenerFirestore = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    renderizarLista()
                    return@addSnapshotListener
                }

                listaPendentes.clear()
                for (doc in snapshot.documents) {
                    val tipo = doc.getString("tipo") ?: "aluno"
                    val status = doc.getString("status") ?: ""

                    if (tipo.equals("aluno", ignoreCase = true) && status.equals("pendente", ignoreCase = true)) {
                        val id = doc.id
                        val nome = doc.getString("nome") ?: "Aluno"
                        val email = doc.getString("email") ?: ""
                        val genero = doc.getString("genero") ?: ""
                        listaPendentes.add(AlunoPendenteItem(id, nome, email, genero))
                    }
                }

                renderizarLista()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerFirestore?.remove()
    }

    private fun renderizarLista() {
        llContainerCadastrosPendentes.removeAllViews()
        tvTotalPendentes.text = "${listaPendentes.size} CADASTROS PENDENTES"

        val density = resources.displayMetrics.density

        if (listaPendentes.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = "Nenhum cadastro pendente de aluno."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
                setPadding(0, (16 * density).toInt(), 0, 0)
            }
            llContainerCadastrosPendentes.addView(tvVazio)
            return
        }

        for (item in listaPendentes) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (16 * density).toInt())
                }
                setBackgroundResource(R.drawable.bg_button_outline)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
            }

            // Row 1: Nome + Badge Pendente
            val topRow = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvNome = TextView(this).apply {
                text = item.nome
                setTextColor(Color.parseColor("#0F172A"))
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            val tvBadgePendente = TextView(this).apply {
                text = "Pendente"
                setTextColor(Color.parseColor("#2558F5"))
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_pill_gray)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F7FF"))
                setPadding((12 * density).toInt(), (4 * density).toInt(), (12 * density).toInt(), (4 * density).toInt())
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            topRow.addView(tvNome)
            topRow.addView(tvBadgePendente)

            // Subtitle
            val isFeminino = item.genero.equals("Feminino", ignoreCase = true)
            val labelSub = if (isFeminino) "Aluna • Cadastro inicial" else "Aluno • Cadastro inicial"

            val tvSub = TextView(this).apply {
                text = labelSub
                setTextColor(Color.parseColor("#2558F5"))
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (4 * density).toInt()
                }
            }

            // Email
            val tvEmail = TextView(this).apply {
                text = item.email
                setTextColor(Color.parseColor("#64748B"))
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (2 * density).toInt()
                }
            }

            // Message
            val tvMsg = TextView(this).apply {
                text = "Solicitação aguardando análise"
                setTextColor(Color.parseColor("#64748B"))
                textSize = 12f
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (4 * density).toInt()
                }
            }

            // Buttons Row
            val buttonsRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (44 * density).toInt()
                ).apply {
                    topMargin = (16 * density).toInt()
                }
            }

            val btnAprovar = Button(this).apply {
                text = "Aprovar"
                isAllCaps = false
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_rounded_button)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#2558F5"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                setOnClickListener {
                    aprovarAluno(item)
                }
            }

            val space = View(this).apply {
                layoutParams = LinearLayout.LayoutParams((12 * density).toInt(), LinearLayout.LayoutParams.MATCH_PARENT)
            }

            val btnRejeitar = Button(this).apply {
                text = "Rejeitar"
                isAllCaps = false
                setTextColor(Color.parseColor("#E53935"))
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_button_outline)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                setOnClickListener {
                    rejeitarAluno(item)
                }
            }

            buttonsRow.addView(btnAprovar)
            buttonsRow.addView(space)
            buttonsRow.addView(btnRejeitar)

            card.addView(topRow)
            card.addView(tvSub)
            card.addView(tvEmail)
            card.addView(tvMsg)
            card.addView(buttonsRow)

            llContainerCadastrosPendentes.addView(card)
        }
    }

    private fun aprovarAluno(item: AlunoPendenteItem) {
        FirebaseFirestore.getInstance().collection("usuarios").document(item.id)
            .update("status", "aprovado")
            .addOnSuccessListener {
                Toast.makeText(this, "Cadastro de ${item.nome} aprovado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao aprovar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun rejeitarAluno(item: AlunoPendenteItem) {
        FirebaseFirestore.getInstance().collection("usuarios").document(item.id)
            .update("status", "rejeitado")
            .addOnSuccessListener {
                Toast.makeText(this, "Cadastro de ${item.nome} rejeitado.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao rejeitar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
