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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        val spGenero = findViewById<Spinner>(R.id.spGenero)
        val genderOptions = arrayOf("Selecionar", "Masculino", "Feminino", "Outro", "Prefiro não informar")
        spGenero?.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderOptions)

        val etNumero = findViewById<EditText>(R.id.etNumero)
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

            validateField(R.id.etNomeCompleto, R.id.tvErrorNomeCompleto)
            validateField(R.id.etCpf, R.id.tvErrorCpf, 11)
            validateField(R.id.etDataNascimento, R.id.tvErrorDataNascimento, 8)
            validateField(R.id.etCelular, R.id.tvErrorCelular, 10)
            validateField(R.id.etEmail, R.id.tvErrorEmail, 5)
            val senha = validateField(R.id.etSenha, R.id.tvErrorSenha, 8)
            val confirm = validateField(R.id.etConfirmarSenha, R.id.tvErrorConfirmarSenha, 8)
            validateField(R.id.etCep, R.id.tvErrorCep, 8)
            validateField(R.id.etEndereco, R.id.tvErrorEndereco, 5)

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

            if (isValid) {
                startActivity(Intent(this, TriagemActivity::class.java))
                finish()
            }
        }
    }
}
