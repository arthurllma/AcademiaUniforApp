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
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.AlunosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class AlunosInstrutorActivity : AppCompatActivity() {

    private val listaAlunos get() = AlunosRepository.listaAlunos

    private lateinit var tvChipTodos: TextView
    private lateinit var tvChipComFicha: TextView
    private lateinit var tvChipSemFicha: TextView
    private lateinit var etBuscarAluno: EditText
    private lateinit var llContainerAlunos: LinearLayout

    private var filtroAtual = "todos" // "todos", "com_ficha", "sem_ficha"

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

        tvChipTodos = findViewById(R.id.tvChipTodos)
        tvChipComFicha = findViewById(R.id.tvChipComFicha)
        tvChipSemFicha = findViewById(R.id.tvChipSemFicha)
        etBuscarAluno = findViewById(R.id.etBuscarAluno)
        llContainerAlunos = findViewById(R.id.llContainerAlunos)

        val filtroExtra = intent.getStringExtra("filtro_status")
        if (!filtroExtra.isNullOrEmpty()) {
            filtroAtual = filtroExtra
        }

        tvChipTodos.setOnClickListener { selecionarFiltro("todos") }
        tvChipComFicha.setOnClickListener { selecionarFiltro("com_ficha") }
        tvChipSemFicha.setOnClickListener { selecionarFiltro("sem_ficha") }

        etBuscarAluno.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderizarLista(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        selecionarFiltro(filtroAtual)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val filtroExtra = intent.getStringExtra("filtro_status")
        if (!filtroExtra.isNullOrEmpty()) {
            selecionarFiltro(filtroExtra)
        }
    }

    private fun selecionarFiltro(filtro: String) {
        filtroAtual = filtro

        val branco = Color.parseColor("#FFFFFF")
        val cinza = Color.parseColor("#64748B")

        tvChipTodos.setBackgroundResource(if (filtro == "todos") R.drawable.bg_pill_blue_solid else R.drawable.bg_pill_gray)
        tvChipTodos.setTextColor(if (filtro == "todos") branco else cinza)

        tvChipComFicha.setBackgroundResource(if (filtro == "com_ficha") R.drawable.bg_pill_blue_solid else R.drawable.bg_pill_gray)
        tvChipComFicha.setTextColor(if (filtro == "com_ficha") branco else cinza)

        tvChipSemFicha.setBackgroundResource(if (filtro == "sem_ficha") R.drawable.bg_pill_blue_solid else R.drawable.bg_pill_gray)
        tvChipSemFicha.setTextColor(if (filtro == "sem_ficha") branco else cinza)

        renderizarLista(etBuscarAluno.text.toString())
    }

    private fun renderizarLista(busca: String) {
        llContainerAlunos.removeAllViews()
        val density = resources.displayMetrics.density

        val listaFiltrada = listaAlunos.filter { aluno ->
            val atendeBusca = aluno.nome.contains(busca, ignoreCase = true) || aluno.cpf.contains(busca, ignoreCase = true)
            val atendeFiltro = when (filtroAtual) {
                "com_ficha" -> aluno.statusFicha == "Ficha Ativa"
                "sem_ficha" -> aluno.statusFicha == "Aguardando Ficha" || aluno.statusFicha == "Ficha Vencida"
                else -> true
            }
            atendeBusca && atendeFiltro
        }

        if (listaFiltrada.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = "Nenhum aluno encontrado para esse filtro."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
                setPadding(0, (16 * density).toInt(), 0, 0)
            }
            llContainerAlunos.addView(tvVazio)
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
                setBackgroundResource(R.drawable.bg_card_blue)
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
                isClickable = true
                isFocusable = true
            }

            val ivAvatar = ImageView(this).apply {
                id = View.generateViewId()
                layoutParams = RelativeLayout.LayoutParams((48 * density).toInt(), (48 * density).toInt()).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                setImageResource(R.drawable.ic_avatar_bordered)
            }

            val infoLayout = LinearLayout(this).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.END_OF, ivAvatar.id)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    marginStart = (12 * density).toInt()
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
                textSize = 12f
            }

            infoLayout.addView(tvNome)
            infoLayout.addView(tvCpf)

            val rightLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
            }

            val (bgBadge, textCor) = when (aluno.statusFicha) {
                "Ficha Ativa" -> R.drawable.bg_badge_green to "#16A34A"
                "Aguardando Ficha" -> R.drawable.bg_badge_yellow to "#D97706"
                else -> R.drawable.bg_badge_red to "#DC2626"
            }

            val tvBadge = TextView(this).apply {
                text = aluno.statusFicha
                setTextColor(Color.parseColor(textCor))
                textSize = 10f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                setBackgroundResource(bgBadge)
                setPadding((10 * density).toInt(), (4 * density).toInt(), (10 * density).toInt(), (4 * density).toInt())
            }

            val ivSeta = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((16 * density).toInt(), (16 * density).toInt()).apply {
                    marginStart = (8 * density).toInt()
                }
                setImageResource(android.R.drawable.ic_media_play)
                rotation = 180f
                setColorFilter(Color.parseColor("#2558F5"))
            }

            rightLayout.addView(tvBadge)
            rightLayout.addView(ivSeta)

            card.addView(ivAvatar)
            card.addView(infoLayout)
            card.addView(rightLayout)

            card.setOnClickListener {
                startActivity(Intent(this, MontarFichaAlunoActivity::class.java))
            }

            llContainerAlunos.addView(card)
        }
    }
}
