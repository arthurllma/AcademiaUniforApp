package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.FuncionarioModel
import br.com.academiaunifortelas1.repository.AdminRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class GerenciarFuncionariosActivity : AppCompatActivity() {

    private lateinit var llContainerFuncionarios: LinearLayout
    private lateinit var tvTotalFuncionarios: TextView
    private lateinit var tvBadgePendentes: TextView
    private lateinit var etBuscar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gerenciar_funcionarios)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }

        findViewById<View>(R.id.cardCadastrosPendentes)?.setOnClickListener {
            startActivity(Intent(this, CadastrosPendentesActivity::class.java))
        }

        llContainerFuncionarios = findViewById(R.id.llContainerFuncionarios)
        tvTotalFuncionarios = findViewById(R.id.tvTotalFuncionarios)
        tvBadgePendentes = findViewById(R.id.tvBadgePendentes)
        etBuscar = findViewById(R.id.etBuscarFuncionario)

        etBuscar.addTextChangedListener(object : TextWatcher {
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
    }

    override fun onResume() {
        super.onResume()
        tvBadgePendentes.text = AdminRepository.cadastrosPendentes.size.toString()
        renderizarLista(etBuscar.text.toString())
    }

    private fun renderizarLista(filtro: String) {
        llContainerFuncionarios.removeAllViews()

        val listaFiltrada = AdminRepository.funcionariosAtivos.filter {
            it.nome.contains(filtro, ignoreCase = true) || it.matricula.contains(filtro, ignoreCase = true) || it.cargo.contains(filtro, ignoreCase = true)
        }

        tvTotalFuncionarios.text = listaFiltrada.size.toString()

        for (func in listaFiltrada) {
            val card = criarCardFuncionario(func)
            llContainerFuncionarios.addView(card)
        }
    }

    private fun criarCardFuncionario(func: FuncionarioModel): View {
        val density = resources.displayMetrics.density

        val relativeLayout = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (12 * density).toInt()
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
            text = func.nome
            setTextColor(Color.parseColor("#0F172A"))
            textSize = 16f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val tvCargoMatricula = TextView(this).apply {
            text = "${func.cargo} • Matrícula: ${func.matricula}"
            setTextColor(Color.parseColor("#64748B"))
            textSize = 13f
        }

        infoLayout.addView(tvNome)
        infoLayout.addView(tvCargoMatricula)

        val tvStatusBadge = TextView(this).apply {
            text = "Ativo"
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

        relativeLayout.addView(infoLayout)
        relativeLayout.addView(tvStatusBadge)

        return relativeLayout
    }
}
