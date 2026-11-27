package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.AlunoModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class GerenciarAlunosAdminActivity : AppCompatActivity() {

    private lateinit var llContainerAlunosAtivos: LinearLayout
    private lateinit var tvTotalAlunos: TextView
    private lateinit var tvBadgePendentesAlunos: TextView
    private lateinit var etBuscarAluno: EditText

    private var listenerFirestore: ListenerRegistration? = null
    private val listaAlunosAtivos = mutableListOf<AlunoModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerenciar_alunos_admin)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }

        findViewById<View>(R.id.cardCadastrosPendentesAlunos)?.setOnClickListener {
            startActivity(Intent(this, CadastrosAlunosAdminActivity::class.java))
        }

        llContainerAlunosAtivos = findViewById(R.id.llContainerAlunosAtivos)
        tvTotalAlunos = findViewById(R.id.tvTotalAlunos)
        tvBadgePendentesAlunos = findViewById(R.id.tvBadgePendentesAlunos)
        etBuscarAluno = findViewById(R.id.etBuscarAluno)

        etBuscarAluno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderizarLista(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

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

        carregarAlunosFirestore()
    }

    private fun carregarAlunosFirestore() {
        val db = FirebaseFirestore.getInstance()
        listenerFirestore = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    renderizarLista(etBuscarAluno.text.toString())
                    return@addSnapshotListener
                }

                listaAlunosAtivos.clear()
                var countPendentes = 0

                for (doc in snapshot.documents) {
                    val tipo = doc.getString("tipo") ?: "aluno"
                    if (tipo.equals("aluno", ignoreCase = true)) {
                        val status = doc.getString("status") ?: "aprovado"
                        if (status.equals("pendente", ignoreCase = true)) {
                            countPendentes++
                        } else if (status.equals("aprovado", ignoreCase = true)) {
                            val id = doc.id
                            val nome = doc.getString("nome") ?: "Aluno"
                            val cpf = doc.getString("cpf") ?: ""
                            val cpfFormatado = if (cpf.isNotEmpty()) "CPF: $cpf" else "Sem CPF"
                            val genero = doc.getString("genero") ?: ""
                            val labelAluno = if (genero.equals("Feminino", ignoreCase = true)) "Aluna" else "Aluno"

                            listaAlunosAtivos.add(
                                AlunoModel(id, nome, "$labelAluno • $cpfFormatado", "Ativo")
                            )
                        }
                    }
                }

                tvBadgePendentesAlunos.text = countPendentes.toString()
                renderizarLista(etBuscarAluno.text.toString())
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerFirestore?.remove()
    }

    private fun renderizarLista(busca: String) {
        llContainerAlunosAtivos.removeAllViews()

        val listaFiltrada = listaAlunosAtivos.filter { aluno ->
            aluno.nome.contains(busca, ignoreCase = true) || aluno.cpf.contains(busca, ignoreCase = true)
        }

        tvTotalAlunos.text = listaFiltrada.size.toString()

        val density = resources.displayMetrics.density

        if (listaFiltrada.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = "Nenhum aluno encontrado."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
                setPadding(0, (16 * density).toInt(), 0, 0)
            }
            llContainerAlunosAtivos.addView(tvVazio)
            return
        }

        for (aluno in listaFiltrada) {
            val card = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (12 * density).toInt())
                }
                setBackgroundResource(R.drawable.bg_button_outline)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F7FF"))
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
            }

            val infoLayout = LinearLayout(this).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            val tvNome = TextView(this).apply {
                text = aluno.nome
                setTextColor(Color.parseColor("#0F172A"))
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvCpf = TextView(this).apply {
                text = aluno.cpf
                setTextColor(Color.parseColor("#64748B"))
                textSize = 13f
            }

            infoLayout.addView(tvNome)
            infoLayout.addView(tvCpf)

            val isFeminino = aluno.cpf.startsWith("Aluna", ignoreCase = true)
            val badgeText = if (isFeminino) "Ativa" else "Ativo"

            val tvBadge = TextView(this).apply {
                text = badgeText
                setTextColor(Color.parseColor("#2558F5"))
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_rounded_field)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
                setPadding((16 * density).toInt(), (6 * density).toInt(), (16 * density).toInt(), (6 * density).toInt())
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            card.addView(infoLayout)
            card.addView(tvBadge)

            llContainerAlunosAtivos.addView(card)
        }
    }
}
