package br.com.academiaunifortelas1.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

object FichaTreinoRepository {

    /**
     * Associa um treino da biblioteca a um aluno no Firestore.
     * Registra dataInicio como Timestamp.now() e calcula dataVencimento
     * se prazoValidadeDias != null.
     */
    fun associarTreinoAoAluno(
        alunoId: String,
        treinoId: String,
        nomeTreino: String,
        prazoValidadeDias: Int?,
        exerciciosA: List<Map<String, Any>> = emptyList(),
        exerciciosB: List<Map<String, Any>> = emptyList(),
        exerciciosC: List<Map<String, Any>> = emptyList(),
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

        val dadosFicha = hashMapOf(
            "alunoId" to alunoId,
            "treinoId" to treinoId,
            "nomeTreino" to nomeTreino,
            "prazoValidadeDias" to prazoValidadeDias,
            "dataInicio" to dataInicio,
            "dataVencimento" to dataVencimento,
            "exerciciosA" to exerciciosA,
            "exerciciosB" to exerciciosB,
            "exerciciosC" to exerciciosC
        )

        val status = calcularStatusFicha(hasFicha = true, dataVencimento = dataVencimento)

        // Salvar documento da ficha do aluno
        db.collection("usuarios").document(alunoId)
            .collection("fichas").document("ficha_atual")
            .set(dadosFicha)
            .addOnSuccessListener {
                // Atualizar statusFicha no cadastro do aluno
                val updates = hashMapOf<String, Any?>(
                    "statusFicha" to status,
                    "hasFicha" to true,
                    "dataVencimento" to dataVencimento
                )
                db.collection("usuarios").document(alunoId)
                    .update(updates)
                    .addOnSuccessListener { onComplete(true, null) }
                    .addOnFailureListener { e -> onComplete(false, e.message) }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    /**
     * Função utilitária para verificar o status da ficha do aluno.
     * - Sem ficha -> "Aguardando Ficha"
     * - Com dataVencimento != null e data atual (Timestamp.now()) for posterior à dataVencimento -> "Ficha Vencida"
     * - Caso contrário -> "Ficha Ativa"
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
