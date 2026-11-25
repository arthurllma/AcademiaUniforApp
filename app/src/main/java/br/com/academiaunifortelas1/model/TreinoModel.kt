package br.com.academiaunifortelas1.model

import com.google.firebase.Timestamp

data class TreinoModel(
    var id: String = "",
    var nome: String = "",
    var objetivo: String = "",
    var nivel: String = "",
    var prazoValidadeDias: Int? = null,
    var exerciciosA: List<Map<String, Any>> = emptyList(),
    var exerciciosB: List<Map<String, Any>> = emptyList(),
    var exerciciosC: List<Map<String, Any>> = emptyList(),
    var criadoEm: Timestamp? = null
)
