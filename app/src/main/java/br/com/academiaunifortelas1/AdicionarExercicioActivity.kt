package br.com.academiaunifortelas1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.academiaunifortelas1.model.ExercicioModel
import br.com.academiaunifortelas1.repository.ExerciciosRepository
import java.io.File
import java.io.FileOutputStream

class AdicionarExercicioActivity : AppCompatActivity() {

    private var selectedImageUri: Uri? = null
    private var isNewImageSelected = false
    private lateinit var ivPreview: ImageView
    private lateinit var tvLabel: TextView
    private lateinit var tvSub: TextView
    private lateinit var tvHeaderTitle: TextView
    private lateinit var btnVerExemploAluno: Button

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            isNewImageSelected = true
            ivPreview.setImageURI(uri)
            ivPreview.visibility = View.VISIBLE
            tvLabel.text = "Imagem Selecionada"
            tvSub.text = "Clique para alterar"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_exercicio)

        findViewById<View>(R.id.btnVoltar)?.setOnClickListener { finish() }
        findViewById<View>(R.id.btnCancelar)?.setOnClickListener { finish() }

        tvHeaderTitle = findViewById(R.id.tvHeaderTitle)
        btnVerExemploAluno = findViewById(R.id.btnVerExemploAluno)

        val etNome = findViewById<EditText>(R.id.etNomeExercicio)
        val spGrupo = findViewById<Spinner>(R.id.spGrupoMuscular)
        val etObs = findViewById<EditText>(R.id.etObservacoes)
        val etVideoUrl = findViewById<EditText>(R.id.etVideoUrl)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarExercicio)

        val grupos = arrayOf("Peito", "Tríceps", "Costas", "Bíceps", "Ombros", "Pernas", "Core")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, grupos)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spGrupo?.adapter = adapter

        ivPreview = findViewById(R.id.ivImagemPreview)
        tvLabel = findViewById(R.id.tvSelectImageLabel)
        tvSub = findViewById(R.id.tvSelectImageSub)

        findViewById<View>(R.id.cardUploadImagem)?.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        val exercicioId = intent.getStringExtra("exercicio_id")
        val exercicioExistente = if (!exercicioId.isNullOrEmpty()) {
            ExerciciosRepository.listaExercicios.find { it.id == exercicioId }
        } else null

        if (exercicioExistente != null) {
            // Modo Edição
            tvHeaderTitle.text = "Editar Exercício"
            btnSalvar.text = "Salvar Alterações"
            btnVerExemploAluno.visibility = View.VISIBLE

            etNome.setText(exercicioExistente.nome)
            etObs.setText(exercicioExistente.observacoes)
            etVideoUrl.setText(exercicioExistente.videoUrl)

            val indexGrupo = grupos.indexOfFirst { it.equals(exercicioExistente.grupoMuscular, ignoreCase = true) }
            if (indexGrupo >= 0) {
                spGrupo.setSelection(indexGrupo)
            }

            if (exercicioExistente.imagemUri.isNotEmpty()) {
                val file = File(exercicioExistente.imagemUri)
                if (file.exists()) {
                    ivPreview.setImageURI(Uri.fromFile(file))
                    ivPreview.visibility = View.VISIBLE
                    tvLabel.text = "Imagem do Exercício"
                    tvSub.text = "Clique para alterar"
                } else {
                    try {
                        val parsedUri = Uri.parse(exercicioExistente.imagemUri)
                        ivPreview.setImageURI(parsedUri)
                        ivPreview.visibility = View.VISIBLE
                        tvLabel.text = "Imagem do Exercício"
                        tvSub.text = "Clique para alterar"
                    } catch (e: Exception) {
                        // Ignora se não for possível abrir
                    }
                }
            }

            btnVerExemploAluno.setOnClickListener {
                val intent = Intent(this, ExemplosAlunoActivity::class.java)
                intent.putExtra("exercicio_id", exercicioExistente.id)
                intent.putExtra("exercicio_nome", exercicioExistente.nome)
                startActivity(intent)
            }
        }

        btnSalvar.setOnClickListener {
            val nome = etNome?.text?.toString()?.trim() ?: ""
            val grupo = spGrupo?.selectedItem?.toString() ?: "Peito"
            val obs = etObs?.text?.toString()?.trim() ?: ""
            var video = etVideoUrl?.text?.toString()?.trim() ?: ""

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do exercício", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (video.isEmpty()) {
                video = "https://www.youtube.com/watch?v=rT7DgCr-3pg"
            }

            val targetId = exercicioExistente?.id ?: System.currentTimeMillis().toString()
            var finalImagePath = exercicioExistente?.imagemUri ?: ""

            // Copia a imagem selecionada da galeria para o armazenamento interno permanente do app
            if (isNewImageSelected && selectedImageUri != null) {
                val savedPath = copiarImagemParaArmazenamentoInterno(selectedImageUri!!, targetId)
                if (savedPath != null) {
                    finalImagePath = savedPath
                }
            }

            if (exercicioExistente != null) {
                // Atualizar exercício existente
                exercicioExistente.nome = nome
                exercicioExistente.grupoMuscular = grupo
                exercicioExistente.observacoes = obs
                exercicioExistente.videoUrl = video
                if (finalImagePath.isNotEmpty()) {
                    exercicioExistente.imagemUri = finalImagePath
                }
                Toast.makeText(this, "Exercício atualizado no catálogo!", Toast.LENGTH_SHORT).show()
            } else {
                // Criar novo exercício
                val novoExercicio = ExercicioModel(
                    id = targetId,
                    nome = nome,
                    grupoMuscular = grupo,
                    observacoes = obs,
                    videoUrl = video,
                    imagemUri = finalImagePath,
                    imageResId = R.drawable.ic_academia_logo
                )
                ExerciciosRepository.listaExercicios.add(0, novoExercicio)
                Toast.makeText(this, "Exercício salvo no catálogo!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    private fun copiarImagemParaArmazenamentoInterno(uri: Uri, targetId: String): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val file = File(filesDir, "exercicio_img_$targetId.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
