package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.TriagemDataManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class TriagemEtapa5Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa5)

        val btnVoltar = findViewById<View>(R.id.btnVoltarTriagem)
        btnVoltar?.setOnClickListener { finish() }

        val cbAceiteTermo = findViewById<CheckBox>(R.id.cbAceiteTermo)
        val btnConcluirTriagem = findViewById<Button>(R.id.btnConcluirTriagem)

        cbAceiteTermo.setOnCheckedChangeListener { _, isChecked ->
            btnConcluirTriagem.isEnabled = isChecked
            btnConcluirTriagem.alpha = if (isChecked) 1.0f else 0.5f
        }

        btnConcluirTriagem.setOnClickListener {
            if (!cbAceiteTermo.isChecked) {
                Toast.makeText(this, "Aceite o termo de responsabilidade para concluir.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val userId = intent.getStringExtra("USER_UID")
                ?: intent.getStringExtra("user_id")
                ?: auth.currentUser?.uid
                ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

            if (userId.isEmpty()) {
                Toast.makeText(this, "Erro: Usuário não identificado para salvar a triagem.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnConcluirTriagem.isEnabled = false

            TriagemDataManager.termoAceito = true

            // Salvar nas preferências locais para acesso imediato/offline
            val prefsTriagem = getSharedPreferences("TriagemDados", MODE_PRIVATE)
            prefsTriagem.edit()
                .putString("experiencia", TriagemDataManager.experiencia)
                .putString("objetivo", TriagemDataManager.objetivo)
                .putString("frequencia", TriagemDataManager.frequencia)
                .putString("peso", TriagemDataManager.peso)
                .putString("altura", TriagemDataManager.altura)
                .putString("imc", TriagemDataManager.imc)
                .putString("statusParq", TriagemDataManager.statusParq)
                .putBoolean("parqRestricoes", TriagemDataManager.parqRestricoes)
                .putString("lesoes", TriagemDataManager.lesoes)
                .putString("esportes", TriagemDataManager.esportes)
                .putString("observacoes", TriagemDataManager.observacoes)
                .apply()

            val pesoDouble = TriagemDataManager.peso.toDoubleOrNull() ?: 0.0
            val alturaDouble = TriagemDataManager.altura.toDoubleOrNull() ?: 0.0
            val imcDouble = TriagemDataManager.imc.replace(",", ".").toDoubleOrNull() ?: 0.0

            val triagemMap = hashMapOf<String, Any>(
                "experiencia" to TriagemDataManager.experiencia,
                "objetivo" to TriagemDataManager.objetivo,
                "frequencia" to TriagemDataManager.frequencia,
                "peso" to pesoDouble,
                "altura" to alturaDouble,
                "imc" to imcDouble,
                "statusParq" to TriagemDataManager.statusParq,
                "lesoes" to TriagemDataManager.lesoes
            )

            val updates = hashMapOf<String, Any>(
                "status" to "pendente",
                "triagemConcluida" to true,
                "isTriagemConcluida" to true,
                "objetivo" to TriagemDataManager.objetivo,
                "frequencia" to TriagemDataManager.frequencia,
                "frequenciaIdeal" to TriagemDataManager.frequencia,
                "experiencia" to TriagemDataManager.experiencia,
                "peso" to TriagemDataManager.peso,
                "altura" to TriagemDataManager.altura,
                "statusParq" to TriagemDataManager.statusParq,
                "parqStatus" to TriagemDataManager.statusParq,
                "parqRestricoes" to TriagemDataManager.parqRestricoes,
                "lesoes" to TriagemDataManager.lesoes,
                "lesao" to TriagemDataManager.lesoes,
                "esportes" to TriagemDataManager.esportes,
                "observacoes" to TriagemDataManager.observacoes,
                "triagem" to triagemMap
            )

            db.collection("usuarios").document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    TriagemDataManager.reset()

                    // signOut é chamado apenas após o salvamento ter sido concluído com sucesso
                    auth.signOut()

                    Toast.makeText(this, "Cadastro e triagem concluídos! Aguardando aprovação do administrador.", Toast.LENGTH_LONG).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    btnConcluirTriagem.isEnabled = true
                    Toast.makeText(this, "Erro ao salvar triagem: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
