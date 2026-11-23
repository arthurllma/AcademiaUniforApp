package br.com.academiaunifortelas1.model

data class ExercicioModel(
    var id: String = "",
    var nome: String = "",
    var grupoMuscular: String = "",
    var observacoes: String = "",
    var videoUrl: String = "",
    var imagemUri: String = "",
    var imageResId: Int = 0
)
