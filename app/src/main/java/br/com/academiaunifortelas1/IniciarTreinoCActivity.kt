package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class IniciarTreinoCActivity : AppCompatActivity() {

    private val exerciciosTreinoC = listOf(
        "Puxada Frontal",
        "Remada Curvada",
        "Rosca Direta",
        "Rosca Martelo"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_iniciar_treino)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        val tvTituloHeader = findViewById<TextView>(R.id.tvTituloTreinoHeader)
        val llContainer = findViewById<LinearLayout>(R.id.llContainerExerciciosIniciar)

        tvTituloHeader?.text = "Treino C — Costas e Bíceps"

        renderizarExercicios(llContainer, exerciciosTreinoC)

        // Bottom nav
        findViewById<View>(R.id.navHome)?.setOnClickListener {
            val intent = Intent(this, HomeAlunoActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun renderizarExercicios(container: LinearLayout?, exercicios: List<String>) {
        if (container == null) return
        container.removeAllViews()
        val density = resources.displayMetrics.density

        for (nomeExercicio in exercicios) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (16 * density).toInt()
                }
                setBackgroundResource(R.drawable.bg_card_blue)
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
            }

            // Top Row: Nome do Exercício + Botão Editar
            val topRow = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val tvNome = TextView(this).apply {
                text = nomeExercicio
                setTextColor(Color.parseColor("#0F172A"))
                textSize = 18f
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
                setPadding((16 * density).toInt(), (8 * density).toInt(), (16 * density).toInt(), (8 * density).toInt())
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
                    val intent = Intent(this@IniciarTreinoCActivity, EditarExercicioActivity::class.java)
                    intent.putExtra("exercicio_nome", nomeExercicio)
                    startActivity(intent)
                }
            }

            topRow.addView(tvNome)
            topRow.addView(btnEditar)

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
                    topMargin = (16 * density).toInt()
                }
                setOnClickListener {
                    val intent = Intent(this@IniciarTreinoCActivity, ExercicioAlunoActivity::class.java)
                    intent.putExtra("exercicio_nome", nomeExercicio)
                    startActivity(intent)
                }
            }

            card.addView(topRow)
            card.addView(btnIniciarExercicio)

            container.addView(card)
        }
    }
}
