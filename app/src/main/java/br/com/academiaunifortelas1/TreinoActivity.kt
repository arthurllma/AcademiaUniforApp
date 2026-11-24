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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class TreinoActivity : AppCompatActivity() {

    private lateinit var tvTabTreinoA: TextView
    private lateinit var tvTabTreinoB: TextView
    private lateinit var tvTabTreinoC: TextView
    private lateinit var tvInfoTreino: TextView
    private lateinit var tvSubtituloTreino: TextView
    private lateinit var llContainerExercicios: LinearLayout

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

    private var abaAtual = "A"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_treino)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        tvTabTreinoA = findViewById(R.id.tvTabTreinoA)
        tvTabTreinoB = findViewById(R.id.tvTabTreinoB)
        tvTabTreinoC = findViewById(R.id.tvTabTreinoC)
        tvInfoTreino = findViewById(R.id.tvInfoTreino)
        tvSubtituloTreino = findViewById(R.id.tvSubtituloTreino)
        llContainerExercicios = findViewById(R.id.llContainerExerciciosTreino)

        tvTabTreinoA.setOnClickListener { selecionarAba("A") }
        tvTabTreinoB.setOnClickListener { selecionarAba("B") }
        tvTabTreinoC.setOnClickListener { selecionarAba("C") }

        findViewById<Button>(R.id.btnIniciarTreino)?.setOnClickListener {
            val targetClass = when (abaAtual) {
                "B" -> IniciarTreinoBActivity::class.java
                "C" -> IniciarTreinoCActivity::class.java
                else -> IniciarTreinoActivity::class.java
            }
            val intent = Intent(this, targetClass)
            intent.putExtra("treino_aba", abaAtual)
            startActivity(intent)
        }

        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, HomeAlunoActivity::class.java)
            startActivity(intent)
            finish()
        }

        selecionarAba("A")
    }

    private fun selecionarAba(aba: String) {
        abaAtual = aba

        val azulAtivo = Color.parseColor("#FFFFFF")
        val azulInativo = Color.parseColor("#2558F5")

        tvTabTreinoA.setBackgroundResource(if (aba == "A") R.drawable.bg_pill_blue else 0)
        tvTabTreinoA.setTextColor(if (aba == "A") azulAtivo else azulInativo)

        tvTabTreinoB.setBackgroundResource(if (aba == "B") R.drawable.bg_pill_blue else 0)
        tvTabTreinoB.setTextColor(if (aba == "B") azulAtivo else azulInativo)

        tvTabTreinoC.setBackgroundResource(if (aba == "C") R.drawable.bg_pill_blue else 0)
        tvTabTreinoC.setTextColor(if (aba == "C") azulAtivo else azulInativo)

        val lista: List<ExercicioItemFicha>
        when (aba) {
            "B" -> {
                tvInfoTreino.text = "ⓘ  Treino B — Pernas e Glúteos"
                tvSubtituloTreino.text = "Foco em membros inferiores e hipertrofia de pernas"
                lista = listaTreinoB
            }
            "C" -> {
                tvInfoTreino.text = "ⓘ  Treino C — Costas e Bíceps"
                tvSubtituloTreino.text = "Foco em dorsais e flexores de cotovelo"
                lista = listaTreinoC
            }
            else -> {
                tvInfoTreino.text = "ⓘ  Treino A — Peito e Tríceps"
                tvSubtituloTreino.text = "Foco em membros superiores e força geral"
                lista = listaTreinoA
            }
        }

        renderizarExercicios(lista)
    }

    private fun renderizarExercicios(lista: List<ExercicioItemFicha>) {
        llContainerExercicios.removeAllViews()
        val density = resources.displayMetrics.density

        for (ex in lista) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (12 * density).toInt()
                }
                setBackgroundResource(R.drawable.bg_card_blue)
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
            }

            // Top Row: Nome + Botão Editar
            val topRow = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvNome = TextView(this).apply {
                text = ex.nome
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

            val btnEditar = TextView(this).apply {
                text = "Editar"
                setTextColor(Color.parseColor("#1D49EE"))
                textSize = 13f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_top_bar_back)
                setPadding((16 * density).toInt(), (6 * density).toInt(), (16 * density).toInt(), (6 * density).toInt())
                isClickable = true
                isFocusable = true
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                setOnClickListener {
                    val intent = Intent(this@TreinoActivity, EditarExercicioActivity::class.java)
                    intent.putExtra("exercicio_nome", ex.nome)
                    startActivity(intent)
                }
            }

            topRow.addView(tvNome)
            topRow.addView(btnEditar)

            // Badges Row: Series/Reps + Carga
            val badgesRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (10 * density).toInt()
                }
            }

            val badgeReps = TextView(this).apply {
                text = ex.seriesReps
                setTextColor(Color.parseColor("#2558F5"))
                textSize = 12f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_badge)
                setPadding((10 * density).toInt(), (4 * density).toInt(), (10 * density).toInt(), (4 * density).toInt())
            }

            val badgeCarga = TextView(this).apply {
                text = ex.carga
                setTextColor(Color.parseColor("#2558F5"))
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

            badgesRow.addView(badgeReps)
            badgesRow.addView(badgeCarga)

            // Botão Iniciar Exercício -> Abre activity_exercicio_aluno.xml
            val btnIniciarExercicio = Button(this).apply {
                text = "Iniciar Exercício"
                isAllCaps = false
                setTextColor(Color.WHITE)
                textSize = 14f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(R.drawable.bg_rounded_button)
                backgroundTintList = null
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (48 * density).toInt()
                ).apply {
                    topMargin = (12 * density).toInt()
                }
                setOnClickListener {
                    val intent = Intent(this@TreinoActivity, ExercicioAlunoActivity::class.java)
                    intent.putExtra("exercicio_nome", ex.nome)
                    startActivity(intent)
                }
            }

            card.addView(topRow)
            card.addView(badgesRow)
            card.addView(btnIniciarExercicio)

            llContainerExercicios.addView(card)
        }
    }
}
