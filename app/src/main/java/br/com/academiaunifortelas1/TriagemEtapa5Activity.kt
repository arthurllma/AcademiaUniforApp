package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TriagemEtapa5Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_triagem_etapa5)

        val cbAceiteTermo = findViewById<CheckBox>(R.id.cbAceiteTermo)
        val btnConcluirTriagem = findViewById<Button>(R.id.btnConcluirTriagem)

        // Habilitar o botao apenas se o CheckBox estiver marcado
        cbAceiteTermo.setOnCheckedChangeListener { _, isChecked ->
            btnConcluirTriagem.isEnabled = isChecked
            btnConcluirTriagem.alpha = if (isChecked) 1.0f else 0.5f
        }

        btnConcluirTriagem.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val userId = auth.currentUser?.uid
                ?: getSharedPreferences("UsuarioPref", MODE_PRIVATE).getString("user_id", "") ?: ""

            if (userId.isNotEmpty()) {
                db.collection("usuarios").document(userId)
                    .update("status", "pendente", "triagemConcluida", true)
            }

            auth.signOut()

            Toast.makeText(this, "Cadastro e triagem concluídos! Aguardando aprovação do administrador.", Toast.LENGTH_LONG).show()

            // Retorna para a tela de Login aguardando autorização do ADM
            val intent = Intent(this, LoginActivity::class.java)
            // Limpa toda a pilha de telas de Triagem (e Cadastro se houver) 
            // garantindo que o botao voltar não retorne para o fluxo de Triagem
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
