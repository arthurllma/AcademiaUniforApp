package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MontarTreinoInstrutorActivity : AppCompatActivity() {

    private data class ExercicioEditavel(
        val nome: String,
        val series: String,
        val reps: String,
        val carga: String,
        val descanso: String
    )

    private val exerciciosA = mutableListOf(
        ExercicioEditavel("Supino Reto", "4", "12", "60 kg", "60 s"),
        ExercicioEditavel("Supino Inclinado", "4", "10", "50 kg", "60 s"),
        ExercicioEditavel("Crucifixo", "3", "12", "16 kg", "45 s"),
        ExercicioEditavel("Tríceps Pulley", "4", "12", "35 kg", "45 s"),
        ExercicioEditavel("Tríceps Francês", "3", "12", "14 kg", "45 s")
    )

    private val exerciciosB = mutableListOf(
        ExercicioEditavel("Leg Press 45", "4", "12", "120 kg", "90 s"),
        ExercicioEditavel("Extensor de Pernas", "4", "12", "45 kg", "60 s"),
        ExercicioEditavel("Flexora Sentada", "3", "15", "40 kg", "60 s"),
        ExercicioEditavel("Adutora", "3", "15", "30 kg", "45 s"),
        ExercicioEditavel("Abdutora", "3", "15", "35 kg", "45 s")
    )

    private val exerciciosC = mutableListOf(
        ExercicioEditavel("Puxada Frontal", "4", "12", "50 kg", "60 s"),
        ExercicioEditavel("Remada Curvada", "4", "10", "40 kg", "60 s"),
        ExercicioEditavel("Rosca Direta", "3", "12", "15 kg", "45 s"),
        ExercicioEditavel("Rosca Martelo", "3", "12", "12 kg", "45 s")
    )

    private var abaAtual = "A"

    private lateinit var tvHeaderTitulo: TextView
    private lateinit var etNomeTreino: EditText
    private lateinit var tvTabA: TextView
    private lateinit var tvTabB: TextView
    private lateinit var tvTabC: TextView
    private lateinit var tvResumoTitulo: TextView
    private lateinit var tvResumoCount: TextView
    private lateinit var llContainerExercicios: LinearLayout
    private lateinit var etSelecionarExercicio: EditText
    private lateinit var etSeries: EditText
    private lateinit var etReps: EditText
    private lateinit var etCarga: EditText
    private lateinit var etDescanso: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_montar_treino_instrutor)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        tvHeaderTitulo = findViewById(R.id.tvHeaderTitulo)
        etNomeTreino = findViewById(R.id.etNomeTreino)
        tvTabA = findViewById(R.id.tvTabA)
        tvTabB = findViewById(R.id.tvTabB)
        tvTabC = findViewById(R.id.tvTabC)
        tvResumoTitulo = findViewById(R.id.tvResumoExerciciosTitulo)
        tvResumoCount = findViewById(R.id.tvResumoExerciciosCount)
        llContainerExercicios = findViewById(R.id.llContainerExerciciosMontar)

        etSelecionarExercicio = findViewById(R.id.etSelecionarExercicio)
        etSeries = findViewById(R.id.etSeries)
        etReps = findViewById(R.id.etReps)
        etCarga = findViewById(R.id.etCarga)
        etDescanso = findViewById(R.id.etDescanso)

        val treinoTipo = intent.getStringExtra("treino_tipo")
        val treinoNome = intent.getStringExtra("treino_nome")

        if (!treinoTipo.isNullOrEmpty()) {
            tvHeaderTitulo.text = "Editar treino"
            if (!treinoNome.isNullOrEmpty()) {
                etNomeTreino.setText(treinoNome)
            }
            abaAtual = treinoTipo
        }

        tvTabA.setOnClickListener { selecionarAba("A") }
        tvTabB.setOnClickListener { selecionarAba("B") }
        tvTabC.setOnClickListener { selecionarAba("C") }

        findViewById<Button>(R.id.btnAdicionarExercicioItem)?.setOnClickListener {
            val nome = etSelecionarExercicio.text.toString().trim()
            if (nome.isNotEmpty()) {
                val series = etSeries.text.toString().ifEmpty { "4" }
                val reps = etReps.text.toString().ifEmpty { "12" }
                val carga = etCarga.text.toString().ifEmpty { "50 kg" }
                val descanso = etDescanso.text.toString().ifEmpty { "60 s" }

                val novoEx = ExercicioEditavel(nome, series, reps, carga, descanso)
                getListaAtiva().add(novoEx)
                etSelecionarExercicio.setText("")
                renderizarListaExercicio()
                Toast.makeText(this, "Exercício adicionado!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Digite o nome do exercício", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnSalvarTreino)?.setOnClickListener {
            Toast.makeText(this, "Treino salvo na biblioteca com sucesso!", Toast.LENGTH_SHORT).show()
            finish()
        }

        selecionarAba(abaAtual)
    }

    private fun getListaAtiva(): MutableList<ExercicioEditavel> {
        return when (abaAtual) {
            "B" -> exerciciosB
            "C" -> exerciciosC
            else -> exerciciosA
        }
    }

    private fun selecionarAba(aba: String) {
        abaAtual = aba

        val azulAtivo = Color.parseColor("#FFFFFF")
        val cinzaInativo = Color.parseColor("#64748B")

        tvTabA.setBackgroundResource(if (aba == "A") R.drawable.bg_pill_blue_solid else 0)
        tvTabA.setTextColor(if (aba == "A") azulAtivo else cinzaInativo)

        tvTabB.setBackgroundResource(if (aba == "B") R.drawable.bg_pill_blue_solid else 0)
        tvTabB.setTextColor(if (aba == "B") azulAtivo else cinzaInativo)

        tvTabC.setBackgroundResource(if (aba == "C") R.drawable.bg_pill_blue_solid else 0)
        tvTabC.setTextColor(if (aba == "C") azulAtivo else cinzaInativo)

        tvResumoTitulo.text = "Exercícios do Treino $aba"

        renderizarListaExercicio()
    }

    private fun renderizarListaExercicio() {
        llContainerExercicios.removeAllViews()
        val lista = getListaAtiva()
        val density = resources.displayMetrics.density

        tvResumoCount.text = "${lista.size} exercícios"

        for (i in lista.indices) {
            val ex = lista[i]

            val card = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = (12 * density).toInt()
                }
                setBackgroundResource(R.drawable.bg_button_outline)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
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
                text = ex.nome
                setTextColor(Color.parseColor("#0F172A"))
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvDetalhes = TextView(this).apply {
                text = "${ex.series} × ${ex.reps} • ${ex.carga} • ${ex.descanso}"
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
            }

            infoLayout.addView(tvNome)
            infoLayout.addView(tvDetalhes)

            // Ações: Editar e Deletar
            val actionsLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            val btnEditar = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((32 * density).toInt(), (32 * density).toInt())
                setBackgroundResource(R.drawable.shape_circle)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#2558F5"))
                setPadding((8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt())
                setImageResource(android.R.drawable.ic_menu_edit)
                imageTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    val intent = Intent(this@MontarTreinoInstrutorActivity, EditarExercicioActivity::class.java)
                    intent.putExtra("exercicio_nome", ex.nome)
                    startActivity(intent)
                }
            }

            val btnDeletar = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((32 * density).toInt(), (32 * density).toInt()).apply {
                    marginStart = (8 * density).toInt()
                }
                setBackgroundResource(R.drawable.shape_circle)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E53935"))
                setPadding((8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt(), (8 * density).toInt())
                setImageResource(android.R.drawable.ic_menu_delete)
                imageTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    lista.removeAt(i)
                    renderizarListaExercicio()
                    Toast.makeText(this@MontarTreinoInstrutorActivity, "Exercício removido", Toast.LENGTH_SHORT).show()
                }
            }

            actionsLayout.addView(btnEditar)
            actionsLayout.addView(btnDeletar)

            card.addView(infoLayout)
            card.addView(actionsLayout)

            llContainerExercicios.addView(card)
        }
    }
}
