package br.com.academiaunifortelas1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.TreinoModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class BibliotecaTreinosInstrutorActivity : AppCompatActivity() {

    private lateinit var llContainerBiblioteca: LinearLayout
    private var listenerFirestore: ListenerRegistration? = null
    private val listaTreinos = mutableListOf<TreinoModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biblioteca_treinos_instrutor)

        llContainerBiblioteca = findViewById(R.id.llContainerBiblioteca)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavInstrutor)
        bottomNav.selectedItemId = R.id.navMontarFicha

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
                R.id.navMontarFicha -> true
                R.id.navAlunos -> {
                    startActivity(Intent(this, AlunosInstrutorActivity::class.java))
                    @Suppress("DEPRECATION")
                    overridePendingTransition(0, 0)
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

        findViewById<Button>(R.id.btnMontarNovoTreino).setOnClickListener {
            startActivity(Intent(this, MontarTreinoInstrutorActivity::class.java))
        }

        carregarBibliotecaFirestore()
    }

    private fun carregarBibliotecaFirestore() {
        val db = FirebaseFirestore.getInstance()
        listenerFirestore = db.collection("biblioteca_treinos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    renderizarLista()
                    return@addSnapshotListener
                }

                listaTreinos.clear()
                for (doc in snapshot.documents) {
                    val model = doc.toObject(TreinoModel::class.java)
                    if (model != null) {
                        model.id = doc.id
                        listaTreinos.add(model)
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
        llContainerBiblioteca.removeAllViews()
        val density = resources.displayMetrics.density

        if (listaTreinos.isEmpty()) {
            val tvVazio = TextView(this).apply {
                text = "Nenhum treino cadastrado na biblioteca."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
                setPadding(0, (16 * density).toInt(), 0, 0)
            }
            llContainerBiblioteca.addView(tvVazio)
            return
        }

        for (treino in listaTreinos) {
            val card = RelativeLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, (12 * density).toInt())
                }
                setBackgroundResource(R.drawable.bg_button_outline)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F1F5F9"))
                setPadding((16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt(), (16 * density).toInt())
                isClickable = true
                isFocusable = true
            }

            val iconBox = View(this).apply {
                id = View.generateViewId()
                layoutParams = RelativeLayout.LayoutParams((32 * density).toInt(), (32 * density).toInt()).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_START)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                }
                setBackgroundResource(R.drawable.shape_circle)
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#DCE4FC"))
            }

            val ivFitness = ImageView(this).apply {
                layoutParams = RelativeLayout.LayoutParams((16 * density).toInt(), (16 * density).toInt()).apply {
                    addRule(RelativeLayout.ALIGN_START, iconBox.id)
                    addRule(RelativeLayout.ALIGN_TOP, iconBox.id)
                    addRule(RelativeLayout.ALIGN_END, iconBox.id)
                    addRule(RelativeLayout.ALIGN_BOTTOM, iconBox.id)
                }
                setPadding((2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt(), (2 * density).toInt())
                setImageResource(R.drawable.ic_fitness)
                setColorFilter(Color.parseColor("#2558F5"))
            }

            val totalEx = treino.getListaA().size + treino.getListaB().size + treino.getListaC().size

            val infoLayout = LinearLayout(this).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.END_OF, iconBox.id)
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    marginStart = (12 * density).toInt()
                }
            }

            val tvNome = TextView(this).apply {
                text = treino.getNomeExibicao()
                setTextColor(Color.parseColor("#0F172A"))
                textSize = 16f
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val tvDetalhes = TextView(this).apply {
                val nivelDesc = if (treino.nivel.isNotEmpty()) " • ${treino.nivel}" else ""
                text = "$totalEx exercícios$nivelDesc"
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

            val btnDeletar = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((24 * density).toInt(), (24 * density).toInt())
                setImageResource(android.R.drawable.ic_menu_delete)
                setColorFilter(Color.parseColor("#EF4444"))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    FirebaseFirestore.getInstance().collection("biblioteca_treinos")
                        .document(treino.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(this@BibliotecaTreinosInstrutorActivity, "Treino removido da biblioteca.", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            actionsLayout.addView(btnDeletar)

            card.addView(iconBox)
            card.addView(ivFitness)
            card.addView(infoLayout)
            card.addView(actionsLayout)

            card.setOnClickListener {
                val intent = Intent(this, MontarTreinoInstrutorActivity::class.java)
                intent.putExtra("treino_tipo", "A")
                intent.putExtra("treino_nome", treino.getNomeExibicao())
                startActivity(intent)
            }

            llContainerBiblioteca.addView(card)
        }
    }
}
