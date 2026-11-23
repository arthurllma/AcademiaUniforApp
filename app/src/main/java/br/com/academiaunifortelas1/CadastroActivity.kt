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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, systemBars.bottom)
                insets
            }
        }

        // 2. Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        val spGenero = findViewById<Spinner>(R.id.spGenero)
        val genderOptions = arrayOf("Selecionar", "Masculino", "Feminino", "Outro", "Prefiro não informar")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, genderOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spGenero?.adapter = adapter

        val etCpf = findViewById<EditText>(R.id.etCpf)
        val etDataNascimento = findViewById<EditText>(R.id.etDataNascimento)
        val etCelular = findViewById<EditText>(R.id.etCelular)
        val etCep = findViewById<EditText>(R.id.etCep)

        // Aplicar máscaras nos campos de texto
        applyMask(etCpf, "###.###.###-##", 11)
        applyMask(etDataNascimento, "##/##/####", 8)
        applyMask(etCelular, "(##) #####-####", 11)
        applyMask(etCep, "#####-###", 8)

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
            val cpf = validateField(R.id.etCpf, R.id.tvErrorCpf, 14)
            val dataNasc = validateField(R.id.etDataNascimento, R.id.tvErrorDataNascimento, 10)
            val celular = validateField(R.id.etCelular, R.id.tvErrorCelular, 14)
            val email = validateField(R.id.etEmail, R.id.tvErrorEmail, 5)
            val senha = validateField(R.id.etSenha, R.id.tvErrorSenha, 8)
            val confirm = validateField(R.id.etConfirmarSenha, R.id.tvErrorConfirmarSenha, 8)
            val cep = validateField(R.id.etCep, R.id.tvErrorCep, 9)
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

                        // 4. Montar os dados para mandar pro Firestore (Banco de Dados com status Pendente)
                        val dadosUsuario = hashMapOf(
                            "id" to userId,
                            "nome" to nome,
                            "cpf" to cpf,
                            "dataNascimento" to dataNasc,
                            "genero" to genero,
                            "celular" to celular,
                            "email" to email,
                            "cep" to cep,
                            "endereco" to endereco,
                            "numero" to numero,
                            "complemento" to complemento,
                            "status" to "pendente"
                        )

                        // 5. Salvar na coleção "usuarios"
                        db.collection("usuarios").document(userId)
                            .set(dadosUsuario)
                            .addOnSuccessListener {
                                val primeiroNome = nome.trim().split(" ").firstOrNull() ?: ""
                                getSharedPreferences("UsuarioPref", MODE_PRIVATE).edit()
                                    .putString("user_id", userId)
                                    .putString("nome_completo", nome)
                                    .putString("primeiro_nome", primeiroNome)
                                    .putString("email", email)
                                    .apply()

                                Toast.makeText(this, "Cadastro realizado! Aguardando aprovação do administrador.", Toast.LENGTH_LONG).show()

                                // Retorna para a tela de Login aguardando liberação
                                val intent = Intent(this, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
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

    private fun applyMask(editText: EditText?, mask: String, maxDigits: Int) {
        editText?.addTextChangedListener(object : android.text.TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val str = s.toString().replace(Regex("[^\\d]"), "")
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                val cleanStr = if (str.length > maxDigits) str.substring(0, maxDigits) else str

                var formatted = ""
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#') {
                        if (i < cleanStr.length) {
                            formatted += m
                        }
                    } else {
                        if (i < cleanStr.length) {
                            formatted += cleanStr[i]
                            i++
                        } else {
                            break
                        }
                    }
                }

                isUpdating = true
                editText.setText(formatted)
                editText.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }
}