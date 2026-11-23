package br.com.academiaunifortelas1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.repository.ExerciciosRepository
import java.io.File

class ExemplosAlunoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exemplos_aluno)

        findViewById<View>(R.id.btnVoltarMenu)?.setOnClickListener { finish() }

        val exercicioId = intent.getStringExtra("exercicio_id")
        val nomeExercicio = intent.getStringExtra("exercicio_nome") ?: "Supino Reto"
        val exercicio = (if (!exercicioId.isNullOrEmpty()) ExerciciosRepository.listaExercicios.find { it.id == exercicioId } else null)
            ?: ExerciciosRepository.buscarPorNome(nomeExercicio)
            ?: ExerciciosRepository.listaExercicios.firstOrNull()

        val tvTitulo = findViewById<TextView>(R.id.tvExercicioTitulo)
        val tvDetalhe = findViewById<TextView>(R.id.tvExercicioDetalhe)
        val ivExemploImagem = findViewById<ImageView>(R.id.ivExemploImagem)
        val cardVideoPlayer = findViewById<View>(R.id.cardVideoPlayer)

        if (exercicio != null) {
            tvTitulo.text = exercicio.nome
            tvDetalhe.text = "${exercicio.grupoMuscular} • ${exercicio.observacoes}"

            // Clique no Card de Vídeo abre no app oficial do YouTube
            cardVideoPlayer.setOnClickListener {
                if (exercicio.videoUrl.isNotEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercicio.videoUrl))
                    startActivity(intent)
                }
            }

            // Configurar a imagem de exemplo
            if (exercicio.imagemUri.isNotEmpty()) {
                val file = File(exercicio.imagemUri)
                if (file.exists()) {
                    ivExemploImagem.setImageURI(Uri.fromFile(file))
                } else {
                    try {
                        ivExemploImagem.setImageURI(Uri.parse(exercicio.imagemUri))
                    } catch (e: Exception) {
                        if (exercicio.imageResId != 0) {
                            ivExemploImagem.setImageResource(exercicio.imageResId)
                        }
                    }
                }
            } else if (exercicio.imageResId != 0) {
                ivExemploImagem.setImageResource(exercicio.imageResId)
            }
        }
    }
}
