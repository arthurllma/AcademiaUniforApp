package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

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
            // Atualizar banco de dados aqui indicando que a triagem do usuario foi concluida

            // Navega pra Home do Aluno
            val intent = Intent(this, HomeAlunoActivity::class.java)
            // Limpa toda a pilha de telas de Triagem (e Cadastro se houver) 
            // garantindo que o botao voltar não retorne para o fluxo de Triagem
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
