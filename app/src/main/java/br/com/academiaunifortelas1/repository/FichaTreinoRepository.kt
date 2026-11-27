package br.com.academiaunifortelas1.repository

import br.com.academiaunifortelas1.model.ExercicioItem
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Calendar

object FichaTreinoRepository {

    /**
     * Associa um treino da biblioteca ou personalizado a um aluno no Firestore.
     * Salva as listas treinoA, treinoB e treinoC no documento usuarios/{alunoId}/fichas/ficha_atual.
     */
    fun associarTreinoAoAluno(
        alunoId: String,
        treinoId: String,
        nomeTreino: String,
        prazoValidadeDias: Int?,
        treinoA: List<ExercicioItem> = emptyList(),
        treinoB: List<ExercicioItem> = emptyList(),
        treinoC: List<ExercicioItem> = emptyList(),
        onComplete: (Boolean, String?) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val dataInicio = Timestamp.now()

        val dataVencimento: Timestamp? = if (prazoValidadeDias != null && prazoValidadeDias > 0) {
            val calendar = Calendar.getInstance().apply {
                time = dataInicio.toDate()
                add(Calendar.DAY_OF_YEAR, prazoValidadeDias)
            }
            Timestamp(calendar.time)
        } else {
            null
        }

        // Mapear para compatibilidade retroativa com exerciciosA/B/C
        val mapA = treinoA.map { mapOf("nome" to it.nome, "series" to it.series, "reps" to it.reps, "carga" to it.carga, "descanso" to it.descanso) }
        val mapB = treinoB.map { mapOf("nome" to it.nome, "series" to it.series, "reps" to it.reps, "carga" to it.carga, "descanso" to it.descanso) }
        val mapC = treinoC.map { mapOf("nome" to it.nome, "series" to it.series, "reps" to it.reps, "carga" to it.carga, "descanso" to it.descanso) }

        val dadosFicha = hashMapOf(
            "alunoId" to alunoId,
            "treinoId" to treinoId,
            "nomeTreino" to nomeTreino,
            "prazoValidadeDias" to prazoValidadeDias,
            "dataInicio" to dataInicio,
            "dataVencimento" to dataVencimento,
            "treinoA" to treinoA,
            "treinoB" to treinoB,
            "treinoC" to treinoC,
            "exerciciosA" to mapA,
            "exerciciosB" to mapB,
            "exerciciosC" to mapC
        )

        val statusCalculado = calcularStatusFicha(hasFicha = true, dataVencimento = dataVencimento)

        // Salvar documento da ficha do aluno
        db.collection("usuarios").document(alunoId)
            .collection("fichas").document("ficha_atual")
            .set(dadosFicha, SetOptions.merge())
            .addOnSuccessListener {
                // Atualizar statusFicha e flags no cadastro do aluno
                val updates = hashMapOf<String, Any?>(
                    "statusFicha" to statusCalculado,
                    "hasFicha" to true,
                    "dataVencimento" to dataVencimento
                )
                db.collection("usuarios").document(alunoId)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener { onComplete(true, null) }
                    .addOnFailureListener { e -> onComplete(false, e.message) }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    /**
     * Busca a ficha atual do aluno em usuarios/{alunoId}/fichas/ficha_atual.
     */
    fun obterFichaAtual(alunoId: String, onResult: (Map<String, Any>?) -> Unit) {
        if (alunoId.isEmpty()) {
            onResult(null)
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios").document(alunoId)
            .collection("fichas").document("ficha_atual")
            .get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    onResult(doc.data)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Função utilitária para verificar o status da ficha do aluno.
     */
    fun calcularStatusFicha(hasFicha: Boolean, dataVencimento: Timestamp?): String {
        if (!hasFicha) {
            return "Aguardando Ficha"
        }
        if (dataVencimento != null) {
            val agora = Timestamp.now()
            if (agora.seconds > dataVencimento.seconds) {
                return "Ficha Vencida"
            }
        }
        return "Ficha Ativa"
    }
}
