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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

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

            val nome = validateField(R.id.etNomeCompleto, R.id.tvErrorNomeCompleto)
            val cpf = validateField(R.id.etCpf, R.id.tvErrorCpf, 14)
            val dataNasc = validateField(R.id.etDataNascimento, R.id.tvErrorDataNascimento, 10)
            val celular = validateField(R.id.etCelular, R.id.tvErrorCelular, 14)
            val email = validateField(R.id.etEmail, R.id.tvErrorEmail, 5)
            val senha = validateField(R.id.etSenha, R.id.tvErrorSenha, 8)
            val confirm = validateField(R.id.etConfirmarSenha, R.id.tvErrorConfirmarSenha, 8)
            val cep = validateField(R.id.etCep, R.id.tvErrorCep, 9)
            val endereco = validateField(R.id.etEndereco, R.id.tvErrorEndereco, 5)

            val numero = etNumero?.text.toString().trim()
            val complemento = etComplemento?.text.toString().trim()
            val genero = if (spGenero?.selectedItemPosition ?: 0 > 0) spGenero?.selectedItem.toString() else ""

            if (senha != confirm && senha.isNotEmpty()) {
                findViewById<TextView>(R.id.tvErrorConfirmarSenha)?.visibility = View.VISIBLE
                isValid = false
            }

            val tvErrorGenero = findViewById<TextView>(R.id.tvErrorGenero)
            if (spGenero?.selectedItemPosition == 0) {
                tvErrorGenero?.visibility = View.VISIBLE
                isValid = false
            } else {
                tvErrorGenero?.visibility = View.GONE
            }

            if (isValid) {
                it.isEnabled = false

                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnSuccessListener { authResult ->
                        val userId = authResult.user?.uid ?: ""

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
                            "status" to "pendente",
                            "tipo" to "aluno",
                            "statusFicha" to "Aguardando Ficha",
                            "isTriagemConcluida" to false
                        )

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

                                Toast.makeText(this, "Cadastro realizado! Preencha a triagem para continuar.", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this, TriagemActivity::class.java)
                                intent.putExtra("USER_UID", userId)
                                intent.putExtra("user_id", userId)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Falha ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                it.isEnabled = true
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro no cadastro: ${e.message}", Toast.LENGTH_LONG).show()
                        it.isEnabled = true
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
