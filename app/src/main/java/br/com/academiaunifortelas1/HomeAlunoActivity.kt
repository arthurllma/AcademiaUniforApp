package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeAlunoActivity : AppCompatActivity() {

    private data class ExercicioItemFicha(
        val nome: String,
        val seriesReps: String,
        val carga: String
    )

    private val listaTreinoA = listOf(
        ExercicioItemFicha("Supino Reto", "4×12", "60kg"),
        ExercicioItemFicha("Supino Inclinado", "4×10", "50kg"),
        ExercicioItemFicha("Crucifixo", "3×12", "16kg"),
        ExercicioItemFicha("Tríceps Pulley", "4×12", "35kg"),
        ExercicioItemFicha("Tríceps Francês", "3×12", "14kg")
    )

    private val listaTreinoB = listOf(
        ExercicioItemFicha("Leg Press 45", "4×12", "120kg"),
        ExercicioItemFicha("Extensor de Pernas", "4×12", "45kg"),
        ExercicioItemFicha("Flexora Sentada", "3×15", "40kg"),
        ExercicioItemFicha("Adutora", "3×15", "30kg"),
        ExercicioItemFicha("Abdutora", "3×15", "35kg")
    )

    private val listaTreinoC = listOf(
        ExercicioItemFicha("Puxada Frontal", "4×12", "50kg"),
        ExercicioItemFicha("Remada Curvada", "4×10", "40kg"),
        ExercicioItemFicha("Rosca Direta", "3×12", "15kg"),
        ExercicioItemFicha("Rosca Martelo", "3×12", "12kg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_aluno)

        carregarSaudacaoUsuario()
        configurarAbasTreinoHome()

        val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipperAluno)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val fabChatMoema = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabChatMoema)

        fabChatMoema?.setOnClickListener {
            val chatbotDialog = ChatbotMoemaDialogFragment()
            chatbotDialog.show(supportFragmentManager, "ChatbotMoemaDialogFragment")
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    viewFlipper.displayedChild = 0
                    fabChatMoema?.visibility = View.VISIBLE
                }
                R.id.navTreinos -> {
                    viewFlipper.displayedChild = 1
                    fabChatMoema?.visibility = View.GONE
                }
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

        // Botao de Ver Exemplos de Exercicio
        findViewById<View>(R.id.btnVerExemplos)?.setOnClickListener {
            startActivity(Intent(this, ExemplosListaAlunoActivity::class.java))
        }

        // Botao de Iniciar Treino
        findViewById<Button>(R.id.btnIniciarTreino)?.setOnClickListener {
            val targetClass = when (abaAtualHome) {
                "B" -> IniciarTreinoBActivity::class.java
                "C" -> IniciarTreinoCActivity::class.java
                else -> IniciarTreinoActivity::class.java
            }
            val intent = Intent(this, targetClass)
            intent.putExtra("treino_aba", abaAtualHome)
            startActivity(intent)
        }

        handleIntent(intent)
    }

    private var abaAtualHome = "A"

    private fun configurarAbasTreinoHome() {
        val tabA = findViewById<TextView>(R.id.tvTabTreinoAHome) ?: return
        val tabB = findViewById<TextView>(R.id.tvTabTreinoBHome) ?: return
        val tabC = findViewById<TextView>(R.id.tvTabTreinoCHome) ?: return
        val tvInfo = findViewById<TextView>(R.id.tvInfoTreinoHome) ?: return
        val container = findViewById<LinearLayout>(R.id.llContainerExerciciosTreinoHome) ?: return

        fun selecionar(aba: String) {
            abaAtualHome = aba
            val azulAtivo = android.graphics.Color.parseColor("#FFFFFF")
            val azulInativo = android.graphics.Color.parseColor("#2558F5")

            tabA.setBackgroundResource(if (aba == "A") R.drawable.bg_pill_blue else 0)
            tabA.setTextColor(if (aba == "A") azulAtivo else azulInativo)

            tabB.setBackgroundResource(if (aba == "B") R.drawable.bg_pill_blue else 0)
            tabB.setTextColor(if (aba == "B") azulAtivo else azulInativo)

            tabC.setBackgroundResource(if (aba == "C") R.drawable.bg_pill_blue else 0)
            tabC.setTextColor(if (aba == "C") azulAtivo else azulInativo)

            val lista = when (aba) {
                "B" -> {
                    tvInfo.text = "ⓘ  Treino B — Pernas e Glúteos"
                    listaTreinoB
                }
                "C" -> {
                    tvInfo.text = "ⓘ  Treino C — Costas e Bíceps"
                    listaTreinoC
                }
                else -> {
                    tvInfo.text = "ⓘ  Treino A — Peito e Tríceps"
                    listaTreinoA
                }
            }

            container.removeAllViews()
            val density = resources.displayMetrics.density

            for (ex in lista) {
                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (12 * density).toInt()
                    }
                    setBackgroundResource(R.drawable.bg_card_blue)
                    setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
                }

                val tvNome = TextView(this).apply {
                    text = ex.nome
                    setTextColor(android.graphics.Color.parseColor("#0F172A"))
                    textSize = 16f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val badgeReps = TextView(this).apply {
                    text = ex.seriesReps
                    setTextColor(android.graphics.Color.parseColor("#2558F5"))
                    textSize = 12f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setBackgroundResource(R.drawable.bg_badge)
                    setPadding((10 * density).toInt(), (4 * density).toInt(), (10 * density).toInt(), (4 * density).toInt())
                }

                val badgeCarga = TextView(this).apply {
                    text = ex.carga
                    setTextColor(android.graphics.Color.parseColor("#2558F5"))
                    textSize = 12f
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setBackgroundResource(R.drawable.bg_badge)
                    setPadding((10 * density).toInt(), (4 * density).toInt(), (10 * density).toInt(), (4 * density).toInt())
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginStart = (8 * density).toInt()
                    }
                }

                card.addView(tvNome)
                card.addView(badgeReps)
                card.addView(badgeCarga)

                card.setOnClickListener {
                    val intent = Intent(this, ExercicioAlunoActivity::class.java)
                    intent.putExtra("exercicio_nome", ex.nome)
                    startActivity(intent)
                }

                container.addView(card)
            }
        }

        tabA.setOnClickListener { selecionar("A") }
        tabB.setOnClickListener { selecionar("B") }
        tabC.setOnClickListener { selecionar("C") }

        selecionar("A")
    }

    private fun carregarSaudacaoUsuario() {
        val tvSaudacao = findViewById<TextView>(R.id.tvSaudacaoAluno) ?: return

        val prefs = getSharedPreferences("UsuarioPref", MODE_PRIVATE)
        val primeiroNomeLocal = prefs.getString("primeiro_nome", "") ?: ""

        if (primeiroNomeLocal.isNotEmpty()) {
            tvSaudacao.text = "Olá, $primeiroNomeLocal!"
        }

        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (!userId.isNullOrEmpty()) {
            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeCompleto = document.getString("nome") ?: ""
                        if (nomeCompleto.isNotEmpty()) {
                            val pNome = nomeCompleto.trim().split(" ").firstOrNull() ?: ""
                            if (pNome.isNotEmpty()) {
                                tvSaudacao.text = "Olá, $pNome!"
                                prefs.edit().putString("primeiro_nome", pNome).putString("nome_completo", nomeCompleto).apply()
                            }
                        }
                    }
                }
        }
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
        carregarSaudacaoUsuario()
        // Garante que a barra selecione o item correto dependendo da view mostrada
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val viewFlipper = findViewById<ViewFlipper>(R.id.viewFlipperAluno)
        when (viewFlipper.displayedChild) {
            0 -> if (bottomNav.selectedItemId != R.id.navHome) bottomNav.selectedItemId = R.id.navHome
            1 -> if (bottomNav.selectedItemId != R.id.navTreinos) bottomNav.selectedItemId = R.id.navTreinos
        }
    }
}
