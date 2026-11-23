package br.com.academiaunifortelas1.model

data class FuncionarioModel(
    var id: String = "",
    var nome: String = "",
    var cargo: String = "",
    var matricula: String = "",
    var email: String = "",
    var cref: String = "",
    var setor: String = "",
    var status: String = "Ativo"
)
