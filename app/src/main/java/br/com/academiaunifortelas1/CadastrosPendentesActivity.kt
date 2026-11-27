package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.FuncionarioModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class CadastrosPendentesActivity : AppCompatActivity() {

    private lateinit var llContainerPendentes: LinearLayout
    private lateinit var tvTotalPendentes: TextView
    private var listenerFirestore: ListenerRegistration? = null
    private val listaPendentes = mutableListOf<FuncionarioModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastros_pendentes)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }

        llContainerPendentes = findViewById(R.id.llContainerPendentes)
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

        carregarCadastrosPendentesFirestore()
    }

    private fun carregarCadastrosPendentesFirestore() {
        val db = FirebaseFirestore.getInstance()
        listenerFirestore = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    renderizarLista()
                    return@addSnapshotListener
                }

                listaPendentes.clear()
                for (doc in snapshot.documents) {
                    val tipo = doc.getString("tipo")?.lowercase() ?: ""
                    val status = doc.getString("status") ?: ""

                    // Filtra EXCLUSIVAMENTE instrutor ou adm pendente
                    if ((tipo == "instrutor" || tipo == "adm") && status.equals("pendente", ignoreCase = true)) {
                        val id = doc.id
                        val nome = doc.getString("nome") ?: "Funcionário"
                        val cargo = if (tipo == "adm") "Administrador" else "Instrutor de Musculação"
                        val email = doc.getString("email") ?: ""
                        val cref = doc.getString("cref") ?: ""

                        listaPendentes.add(
                            FuncionarioModel(id, nome, cargo, "", email, cref, "Geral")
                        )
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
        llContainerPendentes.removeAllViews()

        if (listaPendentes.isEmpty()) {
            tvTotalPendentes.text = "NENHUM CADASTRO PENDENTE"
            val tvEmpty = TextView(this).apply {
                text = "Não há solicitações de funcionários pendentes no momento."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
                setPadding(0, 32, 0, 0)
            }
            llContainerPendentes.addView(tvEmpty)
            return
        }

        tvTotalPendentes.text = "${listaPendentes.size} CADASTROS PENDENTES"

        for (func in listaPendentes) {
            val card = criarCardPendente(func)
            llContainerPendentes.addView(card)
        }
    }

    private fun criarCardPendente(func: FuncionarioModel): View {
        val density = resources.displayMetrics.density

        val containerCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (12 * density).toInt()
            }
            setBackgroundResource(R.drawable.bg_button_outline)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
        }

        val topRow = RelativeLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvNome = TextView(this).apply {
            text = func.nome
            setTextColor(Color.parseColor("#0F172A"))
            textSize = 16f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_START)
            }
        }

        val tvBadgePendente = TextView(this).apply {
            text = "Pendente"
            setTextColor(Color.parseColor("#2558F5"))
            textSize = 12f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setBackgroundResource(R.drawable.bg_rounded_field)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F0F7FF"))
            setPadding((12 * density).toInt(), (4 * density).toInt(), (12 * density).toInt(), (4 * density).toInt())
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                addRule(RelativeLayout.ALIGN_PARENT_END)
            }
        }

        topRow.addView(tvNome)
        topRow.addView(tvBadgePendente)

        val detalheCargo = when {
            func.cref.isNotEmpty() -> "${func.cargo} • CREF: ${func.cref}"
            else -> func.cargo
        }

        val tvCargo = TextView(this).apply {
            text = detalheCargo
            setTextColor(Color.parseColor("#17218B"))
            textSize = 13f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setPadding(0, (4 * density).toInt(), 0, 0)
        }

        val tvEmail = TextView(this).apply {
            text = func.email
            setTextColor(Color.parseColor("#64748B"))
            textSize = 13f
            setPadding(0, (2 * density).toInt(), 0, 0)
        }

        val tvSub = TextView(this).apply {
            text = "Solicitação aguardando análise"
            setTextColor(Color.parseColor("#64748B"))
            textSize = 13f
            setPadding(0, (4 * density).toInt(), 0, (12 * density).toInt())
        }

        val buttonsRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val btnAprovar = Button(this).apply {
            text = "Aprovar"
            isAllCaps = false
            setTextColor(Color.WHITE)
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setBackgroundResource(R.drawable.bg_rounded_button)
            backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#1D49EE"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                (46 * density).toInt(),
                1f
            )
            setOnClickListener {
                FirebaseFirestore.getInstance().collection("usuarios").document(func.id)
                    .update("status", "aprovado")
                    .addOnSuccessListener {
                        Toast.makeText(this@CadastrosPendentesActivity, "Cadastro de ${func.nome} aprovado!", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val space = android.widget.Space(this).apply {
            layoutParams = LinearLayout.LayoutParams((12 * density).toInt(), 1)
        }

        val btnRejeitar = Button(this).apply {
            text = "Rejeitar"
            isAllCaps = false
            setTextColor(Color.parseColor("#E53935"))
            textSize = 14f
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            setBackgroundResource(R.drawable.bg_button_outline_red)
            backgroundTintList = null
            layoutParams = LinearLayout.LayoutParams(
                0,
                (46 * density).toInt(),
                1f
            )
            setOnClickListener {
                FirebaseFirestore.getInstance().collection("usuarios").document(func.id)
                    .update("status", "rejeitado")
                    .addOnSuccessListener {
                        Toast.makeText(this@CadastrosPendentesActivity, "Cadastro rejeitado!", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        buttonsRow.addView(btnAprovar)
        buttonsRow.addView(space)
        buttonsRow.addView(btnRejeitar)

        containerCard.addView(topRow)
        containerCard.addView(tvCargo)
        containerCard.addView(tvEmail)
        containerCard.addView(tvSub)
        containerCard.addView(buttonsRow)

        return containerCard
    }
}
