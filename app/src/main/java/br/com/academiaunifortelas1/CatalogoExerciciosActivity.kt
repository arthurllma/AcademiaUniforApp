package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.ExercicioModel
import br.com.academiaunifortelas1.repository.ExerciciosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class CatalogoExerciciosActivity : AppCompatActivity() {

    private lateinit var llContainerExercicios: LinearLayout
    private lateinit var tvTotalExercicios: TextView
    private lateinit var etPesquisar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalogo_exercicios)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }

        findViewById<Button>(R.id.btnAdicionarExercicio)?.setOnClickListener {
            startActivity(Intent(this, AdicionarExercicioActivity::class.java))
        }

        llContainerExercicios = findViewById(R.id.llContainerExercicios)
        tvTotalExercicios = findViewById(R.id.tvTotalExercicios)
        etPesquisar = findViewById(R.id.etPesquisar)

        etPesquisar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderizarLista(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navMontarFicha

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navPainel -> {
                    val intent = Intent(this, HomeInstrutorActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navMontarFicha -> true
                R.id.navAlunos -> {
                    startActivity(Intent(this, AlunosInstrutorActivity::class.java))
                    finish()
                    true
                }
                R.id.navPerfilInstrutor -> {
                    startActivity(Intent(this, PerfilInstrutorActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        renderizarLista(etPesquisar.text.toString())
    }

    private fun renderizarLista(filtro: String) {
        llContainerExercicios.removeAllViews()

        val listaFiltrada = ExerciciosRepository.listaExercicios.filter {
            it.nome.contains(filtro, ignoreCase = true) || it.grupoMuscular.contains(filtro, ignoreCase = true)
        }

        tvTotalExercicios.text = "TODOS OS EXERCÍCIOS • ${listaFiltrada.size}"

        for (exercicio in listaFiltrada) {
            val card = criarCardExercicio(exercicio)
            llContainerExercicios.addView(card)
        }
    }

    private fun criarCardExercicio(exercicio: ExercicioModel): View {
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
            isClickable = true
            isFocusable = true
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
            text = exercicio.nome
            setTextColor(Color.parseColor("#0F172A"))
            textSize = 16f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val tvGrupo = TextView(this).apply {
            text = exercicio.grupoMuscular
            setTextColor(Color.parseColor("#2558F5"))
            textSize = 13f
        }

        infoLayout.addView(tvNome)
        infoLayout.addView(tvGrupo)

        val ivSeta = TextView(this).apply {
            text = "›"
            setTextColor(Color.parseColor("#1D49EE"))
            textSize = 24f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
        }

        relativeLayout.addView(infoLayout)
        relativeLayout.addView(ivSeta)

        relativeLayout.setOnClickListener {
            val intent = Intent(this, AdicionarExercicioActivity::class.java)
            intent.putExtra("exercicio_id", exercicio.id)
            startActivity(intent)
        }

        return relativeLayout
    }
}
