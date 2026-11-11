package br.com.academiaunifortelas1

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MontarTreinoInstrutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_montar_treino_instrutor)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }
    }
}
