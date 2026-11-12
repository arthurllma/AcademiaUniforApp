package br.com.academiaunifortelas1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    // 1. Declarar instâncias do Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        // 2. Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        val spGenero = findViewById<Spinner>(R.id.spGenero)
        val genderOptions = arrayOf("Selecionar", "Masculino", "Feminino", "Outro", "Prefiro não informar")
        spGenero?.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)

        val etNumero = findViewById<EditText>(R.id.etNumero)
        val etComplemento = findViewById<EditText>(R.id.etComplemento)

        findViewById<CheckBox>(R.id.cbSemNumero)?.setOnCheckedChangeListener { _, isChecked ->
            etNumero?.isEnabled = !isChecked
            if (isChecked) etNumero?.text?.clear()
        }

        findViewById<Button>(R.id.btnCriarConta)?.setOnClickListener {
            var isValid = true

            // Funcao helper para validar
            fun validateField(etId: Int, tvErrorId: Int, minLength: Int = 1): String {
                val text = findViewById<EditText>(etId)?.text?.toString()?.trim() ?: ""
                val errorView = findViewById<TextView>(tvErrorId)
                if (text.length < minLength) {
                    errorView?.visibility = View.VISIBLE
                    isValid = false
                } else {
                    errorView?.visibility = View.GONE
                }
                return text
            }

            // Captura todos os textos enquanto valida
            val nome = validateField(R.id.etNomeCompleto, R.id.tvErrorNomeCompleto)
            val cpf = validateField(R.id.etCpf, R.id.tvErrorCpf, 11)
            val dataNasc = validateField(R.id.etDataNascimento, R.id.tvErrorDataNascimento, 8)
            val celular = validateField(R.id.etCelular, R.id.tvErrorCelular, 10)
            val email = validateField(R.id.etEmail, R.id.tvErrorEmail, 5)
            val senha = validateField(R.id.etSenha, R.id.tvErrorSenha, 8)
            val confirm = validateField(R.id.etConfirmarSenha, R.id.tvErrorConfirmarSenha, 8)
            val cep = validateField(R.id.etCep, R.id.tvErrorCep, 8)
            val endereco = validateField(R.id.etEndereco, R.id.tvErrorEndereco, 5)

            // Campos não obrigatórios / tratados de forma diferente
            val numero = etNumero?.text.toString().trim()
            val complemento = etComplemento?.text.toString().trim()
            val genero = if (spGenero?.selectedItemPosition ?: 0 > 0) spGenero?.selectedItem.toString() else ""

            // Validar senhas coincidem
            if (senha != confirm && senha.isNotEmpty()) {
                findViewById<TextView>(R.id.tvErrorConfirmarSenha)?.visibility = View.VISIBLE
                isValid = false
            }

            // Validar Genero
            val tvErrorGenero = findViewById<TextView>(R.id.tvErrorGenero)
            if (spGenero?.selectedItemPosition == 0) {
                tvErrorGenero?.visibility = View.VISIBLE
                isValid = false
            } else {
                tvErrorGenero?.visibility = View.GONE
            }

            // Se tudo estiver preenchido corretamente
            if (isValid) {

                // Desabilitar o botão para o usuário não clicar duas vezes seguidas
                it.isEnabled = false

                // 3. Criar usuário no Firebase Authentication (Apenas com Email e Senha)
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnSuccessListener { authResult ->

                        val userId = authResult.user?.uid ?: ""

                        // 4. Montar os dados para mandar pro Firestore (Banco de Dados)
                        val novoUsuario = Usuario(
                            id = userId,
                            nome = nome,
                            cpf = cpf,
                            dataNascimento = dataNasc,
                            genero = genero,
                            celular = celular,
                            email = email,
                            cep = cep,
                            endereco = endereco,
                            numero = numero,
                            complemento = complemento
                        )

                        // 5. Salvar na coleção "usuarios"
                        db.collection("usuarios").document(userId)
                            .set(novoUsuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()

                                // Navega para a próxima tela
                                startActivity(Intent(this, TriagemActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Falha ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                it.isEnabled = true // reabilita botão
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro no cadastro: ${e.message}", Toast.LENGTH_LONG).show()
                        it.isEnabled = true // reabilita botão
                    }
            }
        }
    }
}