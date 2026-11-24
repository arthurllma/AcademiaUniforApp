package br.com.academiaunifortelas1.model

data class AlunoModel(
    val id: String,
    val nome: String,
    val cpf: String,
    val statusFicha: String // "Ficha Ativa", "Aguardando Ficha", "Ficha Vencida"
)
