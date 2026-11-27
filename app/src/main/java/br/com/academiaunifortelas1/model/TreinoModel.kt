package br.com.academiaunifortelas1.model

import com.google.firebase.Timestamp

data class TreinoModel(
    var id: String = "",
    var nomeTreino: String = "",
    var nome: String = "",
    var objetivo: String = "",
    var nivel: String = "",
    var prazoValidadeDias: Int? = null,
    var criadoPor: String = "",
    var criadoEm: Timestamp? = null,
    var treinoA: List<ExercicioItem> = emptyList(),
    var treinoB: List<ExercicioItem> = emptyList(),
    var treinoC: List<ExercicioItem> = emptyList(),
    var exerciciosA: List<Map<String, Any>> = emptyList(),
    var exerciciosB: List<Map<String, Any>> = emptyList(),
    var exerciciosC: List<Map<String, Any>> = emptyList()
) {
    fun getNomeExibicao(): String {
        return nomeTreino.ifEmpty { nome }
    }

    fun getListaA(): List<ExercicioItem> {
        if (treinoA.isNotEmpty()) return treinoA
        return exerciciosA.map {
            ExercicioItem(
                nome = it["nome"]?.toString() ?: "",
                series = it["series"]?.toString() ?: "",
                reps = it["reps"]?.toString() ?: "",
                carga = it["carga"]?.toString() ?: "",
                descanso = it["descanso"]?.toString() ?: ""
            )
        }
    }

    fun getListaB(): List<ExercicioItem> {
        if (treinoB.isNotEmpty()) return treinoB
        return exerciciosB.map {
            ExercicioItem(
                nome = it["nome"]?.toString() ?: "",
                series = it["series"]?.toString() ?: "",
                reps = it["reps"]?.toString() ?: "",
                carga = it["carga"]?.toString() ?: "",
                descanso = it["descanso"]?.toString() ?: ""
            )
        }
    }

    fun getListaC(): List<ExercicioItem> {
        if (treinoC.isNotEmpty()) return treinoC
        return exerciciosC.map {
            ExercicioItem(
                nome = it["nome"]?.toString() ?: "",
                series = it["series"]?.toString() ?: "",
                reps = it["reps"]?.toString() ?: "",
                carga = it["carga"]?.toString() ?: "",
                descanso = it["descanso"]?.toString() ?: ""
            )
        }
    }
}
