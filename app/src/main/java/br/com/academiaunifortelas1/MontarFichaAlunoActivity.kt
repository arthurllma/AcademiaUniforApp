package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.AlunoModel
import br.com.academiaunifortelas1.model.ExercicioItem
import br.com.academiaunifortelas1.model.TreinoModel
import br.com.academiaunifortelas1.repository.AlunosRepository
import br.com.academiaunifortelas1.repository.ExerciciosRepository
import br.com.academiaunifortelas1.repository.FichaTreinoRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MontarFichaAlunoActivity : AppCompatActivity() {

    private val exerciciosA = mutableListOf<ExercicioItem>()
    private val exerciciosB = mutableListOf<ExercicioItem>()
    private val exerciciosC = mutableListOf<ExercicioItem>()

    private var abaAtual = "A"
    private var alunoId: String = ""
    private val listaTreinosBiblioteca = mutableListOf<TreinoModel>()

    private lateinit var tvNomeHeader: TextView
    private lateinit var tvNomeAluno: TextView
    private lateinit var tvStatusFicha: TextView
    private lateinit var tvObjetivoAluno: TextView
    private lateinit var tvFrequenciaAluno: TextView
    private lateinit var tvTagParq: TextView
    private lateinit var tvTagLesao: TextView

    private lateinit var spUsarTreinoPronto: Spinner
    private lateinit var spPrazoVencimento: Spinner
    private lateinit var spSelecionarExercicio: Spinner

    private lateinit var tvTabA: TextView
    private lateinit var tvTabB: TextView
    private lateinit var tvTabC: TextView

    private lateinit var etSeries: EditText
    private lateinit var etReps: EditText
    private lateinit var etCarga: EditText
    private lateinit var btnAdicionarExercicio: Button
    private lateinit var llContainerExercicios: LinearLayout
    private lateinit var btnSalvarFicha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_montar_ficha_aluno)

        alunoId = intent.getStringExtra("ALUNO_ID")
            ?: intent.getStringExtra("aluno_id")
            ?: ""

        findViewById<ImageView>(R.id.ivBack).setOnClickListener { finish() }

        initViews()
        configurarBottomNav()
        configurarSpinners()
        configurarAbas()

        carregarDadosAluno()
        carregarBibliotecaTreinos()

        btnAdicionarExercicio.setOnClickListener { adicionarExercicio() }
        btnSalvarFicha.setOnClickListener { salvarFicha() }

        selecionarAba("A")
    }

    private fun initViews() {
        tvNomeHeader = findViewById(R.id.tvNomeHeader)
        tvNomeAluno = findViewById(R.id.tvNomeAluno)
        tvStatusFicha = findViewById(R.id.tvStatusFicha)
        tvObjetivoAluno = findViewById(R.id.tvObjetivoAluno)
        tvFrequenciaAluno = findViewById(R.id.tvFrequenciaAluno)
        tvTagParq = findViewById(R.id.tvTagParq)
        tvTagLesao = findViewById(R.id.tvTagLesao)

        spUsarTreinoPronto = findViewById(R.id.spUsarTreinoPronto)
        spPrazoVencimento = findViewById(R.id.spPrazoVencimento)
        spSelecionarExercicio = findViewById(R.id.spSelecionarExercicio)

        tvTabA = findViewById(R.id.tvTabA)
        tvTabB = findViewById(R.id.tvTabB)
        tvTabC = findViewById(R.id.tvTabC)

        etSeries = findViewById(R.id.etSeries)
        etReps = findViewById(R.id.etReps)
        etCarga = findViewById(R.id.etCarga)
        btnAdicionarExercicio = findViewById(R.id.btnAdicionarExercicio)
        llContainerExercicios = findViewById(R.id.llContainerExercicios)
        btnSalvarFicha = findViewById(R.id.btnSalvarFicha)
    }

    private fun configurarBottomNav() {
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
                R.id.navAlunos -> {
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
    }

    private fun configurarSpinners() {
        val prazos = arrayOf(
            "Sem vencimento (padrão)",
            "1 mês (30 dias)",
            "2 meses (60 dias)",
            "3 meses (90 dias)",
            "6 meses (180 dias)"
        )
        val adapterPrazo = ArrayAdapter(this, R.layout.spinner_item, prazos)
        adapterPrazo.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spPrazoVencimento.adapter = adapterPrazo

        val nomesExercicios = ExerciciosRepository.listaExercicios.map { it.nome }
        val adapterExercicios = ArrayAdapter(this, R.layout.spinner_item, nomesExercicios)
        adapterExercicios.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spSelecionarExercicio.adapter = adapterExercicios
    }

    private fun configurarAbas() {
        tvTabA.setOnClickListener { selecionarAba("A") }
        tvTabB.setOnClickListener { selecionarAba("B") }
        tvTabC.setOnClickListener { selecionarAba("C") }
    }

    private fun carregarDadosAluno() {
        if (alunoId.isEmpty()) {
            val fallbackAluno = AlunosRepository.listaAlunos.firstOrNull() ?: AlunoModel("1", "Aluno Exemplo", "CPF: 123", "Aguardando Ficha")
            exibirDadosAluno(
                nome = fallbackAluno.nome,
                statusFicha = fallbackAluno.statusFicha,
                objetivo = "Hipertrofia",
                frequencia = "4x / semana",
                statusParq = "Sem restrições",
                lesoes = "Sem lesões"
            )
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(alunoId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val nome = doc.getString("nome") ?: doc.getString("name") ?: "Aluno"
                    val dataVencimento = doc.getTimestamp("dataVencimento")
                    val hasFicha = doc.get("hasFicha") as? Boolean
                        ?: (doc.getString("statusFicha") != null && doc.getString("statusFicha") != "Aguardando Ficha")

                    val statusCalculado = doc.getString("statusFicha")
                        ?: FichaTreinoRepository.calcularStatusFicha(hasFicha, dataVencimento)

                    val statusFichaFormatado = if (statusCalculado == "Aguardando Ficha") {
                        "Aguardando primeira ficha"
                    } else {
                        statusCalculado
                    }

                    val triagem = doc.get("triagem") as? Map<*, *>

                    val objetivo = (triagem?.get("objetivo") as? String)
                        ?.takeIf { it.isNotEmpty() }
                        ?: doc.getString("objetivo")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "Não informado"

                    val frequencia = (triagem?.get("frequencia") as? String)
                        ?.takeIf { it.isNotEmpty() }
                        ?: doc.getString("frequencia")
                        ?.takeIf { it.isNotEmpty() }
                        ?: doc.getString("frequenciaIdeal")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "Não informado"

                    val statusParq = (triagem?.get("statusParq") as? String)
                        ?.takeIf { it.isNotEmpty() }
                        ?: doc.getString("statusParq")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "Sem restrições"

                    val lesoes = (triagem?.get("lesoes") as? String)
                        ?.takeIf { it.isNotEmpty() }
                        ?: doc.getString("lesoes")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "Sem lesões"

                    exibirDadosAluno(nome, statusFichaFormatado, objetivo, frequencia, statusParq, lesoes)

                    // Carregar a ficha atual do aluno se já possuir
                    carregarFichaAtualAluno()
                } else {
                    val local = AlunosRepository.listaAlunos.find { it.id == alunoId }
                    exibirDadosAluno(
                        nome = local?.nome ?: "Aluno",
                        statusFicha = local?.statusFicha ?: "Aguardando primeira ficha",
                        objetivo = "Não informado",
                        frequencia = "Não informado",
                        statusParq = "Sem restrições",
                        lesoes = "Sem lesões"
                    )
                }
            }
            .addOnFailureListener {
                val local = AlunosRepository.listaAlunos.find { it.id == alunoId }
                exibirDadosAluno(
                    nome = local?.nome ?: "Aluno",
                    statusFicha = local?.statusFicha ?: "Aguardando primeira ficha",
                    objetivo = "Não informado",
                    frequencia = "Não informado",
                    statusParq = "Sem restrições",
                    lesoes = "Sem lesões"
                )
            }
    }

    private fun carregarFichaAtualAluno() {
        FichaTreinoRepository.obterFichaAtual(alunoId) { dados ->
            if (dados != null) {
                fun parseLista(keyNew: String, keyOld: String): List<ExercicioItem> {
                    val listRaw = (dados[keyNew] ?: dados[keyOld]) as? List<*> ?: return emptyList()
                    return listRaw.mapNotNull { item ->
                        when (item) {
                            is Map<*, *> -> ExercicioItem(
                                nome = item["nome"]?.toString() ?: "",
                                series = item["series"]?.toString() ?: "4",
                                reps = item["reps"]?.toString() ?: "12",
                                carga = item["carga"]?.toString() ?: "",
                                descanso = item["descanso"]?.toString() ?: ""
                            )
                            else -> null
                        }
                    }
                }

                val listA = parseLista("treinoA", "exerciciosA")
                val listB = parseLista("treinoB", "exerciciosB")
                val listC = parseLista("treinoC", "exerciciosC")

                if (listA.isNotEmpty() || listB.isNotEmpty() || listC.isNotEmpty()) {
                    exerciciosA.clear()
                    exerciciosA.addAll(listA)

                    exerciciosB.clear()
                    exerciciosB.addAll(listB)

                    exerciciosC.clear()
                    exerciciosC.addAll(listC)

                    val prazo = (dados["prazoValidadeDias"] as? Number)?.toInt()
                    if (prazo != null) {
                        val pos = when (prazo) {
                            30 -> 1
                            60 -> 2
                            90 -> 3
                            180 -> 4
                            else -> 0
                        }
                        spPrazoVencimento.setSelection(pos)
                    }

                    renderizarListaExercicios()
                }
            }
        }
    }

    private fun exibirDadosAluno(
        nome: String,
        statusFicha: String,
        objetivo: String,
        frequencia: String,
        statusParq: String,
        lesoes: String
    ) {
        tvNomeHeader.text = nome
        tvNomeAluno.text = nome
        tvStatusFicha.text = statusFicha
        tvObjetivoAluno.text = objetivo
        tvFrequenciaAluno.text = frequencia

        if (statusParq.equals("Sem restrições", ignoreCase = true) || statusParq.isEmpty()) {
            tvTagParq.text = "PAR-Q: Sem restrições"
            tvTagParq.setBackgroundResource(R.drawable.bg_badge_green)
            tvTagParq.setTextColor(Color.parseColor("#16A34A"))
        } else {
            tvTagParq.text = "PAR-Q: $statusParq"
            tvTagParq.setBackgroundResource(R.drawable.bg_badge_yellow)
            tvTagParq.setTextColor(Color.parseColor("#D97706"))
        }

        if (lesoes.equals("Sem lesões", ignoreCase = true) || lesoes.equals("Nenhuma", ignoreCase = true) || lesoes.isEmpty()) {
            tvTagLesao.text = "Lesão: Sem lesões"
            tvTagLesao.setBackgroundResource(R.drawable.bg_badge_green)
            tvTagLesao.setTextColor(Color.parseColor("#16A34A"))
        } else {
            tvTagLesao.text = "Lesão: $lesoes"
            tvTagLesao.setBackgroundResource(R.drawable.bg_badge_yellow)
            tvTagLesao.setTextColor(Color.parseColor("#D97706"))
        }
    }

    private fun carregarBibliotecaTreinos() {
        FirebaseFirestore.getInstance().collection("biblioteca_treinos")
            .addSnapshotListener { snapshot, error ->
                listaTreinosBiblioteca.clear()
                if (error == null && snapshot != null && !snapshot.isEmpty) {
                    for (doc in snapshot.documents) {
                        val model = doc.toObject(TreinoModel::class.java)
                        if (model != null) {
                            model.id = doc.id
                            listaTreinosBiblioteca.add(model)
                        }
                    }
                }
                populardropdownTreinosProntos()
            }
    }

    private fun populardropdownTreinosProntos() {
        val titulos = mutableListOf("Selecionar treino da biblioteca")
        titulos.addAll(listaTreinosBiblioteca.map { it.getNomeExibicao() })

        val adapter = ArrayAdapter(this, R.layout.spinner_item, titulos)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spUsarTreinoPronto.adapter = adapter

        spUsarTreinoPronto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && position <= listaTreinosBiblioteca.size) {
                    val treino = listaTreinosBiblioteca[position - 1]
                    carregarTreinoPronto(treino)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun carregarTreinoPronto(treino: TreinoModel) {
        exerciciosA.clear()
        exerciciosA.addAll(treino.getListaA())

        exerciciosB.clear()
        exerciciosB.addAll(treino.getListaB())

        exerciciosC.clear()
        exerciciosC.addAll(treino.getListaC())

        val prazoDias = treino.prazoValidadeDias
        if (prazoDias != null) {
            val positionPrazo = when (prazoDias) {
                30 -> 1
                60 -> 2
                90 -> 3
                180 -> 4
                else -> 0
            }
            spPrazoVencimento.setSelection(positionPrazo)
        } else {
            spPrazoVencimento.setSelection(0)
        }

        renderizarListaExercicios()
        Toast.makeText(this, "Treino '${treino.getNomeExibicao()}' carregado!", Toast.LENGTH_SHORT).show()
    }

    private fun getListaAtiva(): MutableList<ExercicioItem> {
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

        renderizarListaExercicios()
    }

    private fun adicionarExercicio() {
        val nome = spSelecionarExercicio.selectedItem?.toString() ?: "Supino Reto"
        val series = etSeries.text.toString().ifEmpty { "4" }
        val reps = etReps.text.toString().ifEmpty { "12" }
        val carga = etCarga.text.toString().ifEmpty { "60kg" }

        val novo = ExercicioItem(nome = nome, series = series, reps = reps, carga = carga, descanso = "60s")
        getListaAtiva().add(novo)
        renderizarListaExercicios()
        Toast.makeText(this, "Exercício adicionado!", Toast.LENGTH_SHORT).show()
    }

    private fun renderizarListaExercicios() {
        llContainerExercicios.removeAllViews()
        val lista = getListaAtiva()
        val density = resources.displayMetrics.density

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
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F9FAFB"))
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
                val desc = if (ex.descanso.isNotEmpty()) " • ${ex.descanso}" else ""
                text = "${ex.series}×${ex.reps} — ${ex.carga}$desc"
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
            }

            infoLayout.addView(tvNome)
            infoLayout.addView(tvDetalhes)

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
                layoutParams = LinearLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt())
                setImageResource(android.R.drawable.ic_menu_edit)
                setColorFilter(Color.parseColor("#64748B"))
                isClickable = true
                isFocusable = true
            }

            val btnDeletar = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt()).apply {
                    marginStart = (16 * density).toInt()
                }
                setImageResource(android.R.drawable.ic_menu_delete)
                setColorFilter(Color.parseColor("#EF4444"))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    lista.removeAt(i)
                    renderizarListaExercicios()
                    Toast.makeText(this@MontarFichaAlunoActivity, "Exercício removido", Toast.LENGTH_SHORT).show()
                }
            }

            actionsLayout.addView(btnEditar)
            actionsLayout.addView(btnDeletar)

            card.addView(infoLayout)
            card.addView(actionsLayout)

            llContainerExercicios.addView(card)
        }
    }

    private fun salvarFicha() {
        if (alunoId.isEmpty()) {
            Toast.makeText(this, "Aluno não identificado!", Toast.LENGTH_SHORT).show()
            return
        }

        val posPrazo = spPrazoVencimento.selectedItemPosition
        val prazoValidadeDias: Int? = when (posPrazo) {
            1 -> 30
            2 -> 60
            3 -> 90
            4 -> 180
            else -> null
        }

        val posTreino = spUsarTreinoPronto.selectedItemPosition
        val (treinoId, nomeTreino) = if (posTreino > 0 && posTreino <= listaTreinosBiblioteca.size) {
            val t = listaTreinosBiblioteca[posTreino - 1]
            t.id to t.getNomeExibicao()
        } else {
            "custom" to "Ficha Personalizada"
        }

        FichaTreinoRepository.associarTreinoAoAluno(
            alunoId = alunoId,
            treinoId = treinoId,
            nomeTreino = nomeTreino,
            prazoValidadeDias = prazoValidadeDias,
            treinoA = exerciciosA,
            treinoB = exerciciosB,
            treinoC = exerciciosC
        ) { success, error ->
            if (success) {
                Toast.makeText(this, "Ficha do aluno salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao salvar ficha: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
