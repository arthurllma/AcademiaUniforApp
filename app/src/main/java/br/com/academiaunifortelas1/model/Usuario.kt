package br.com.academiaunifortelas1.model

data class Usuario(
    var id: String = "",       // ID gerado pelo Firebase Auth
    var nome: String = "",
    var cpf: String = "",
    var dataNascimento: String = "",
    var genero: String = "",
    var celular: String = "",
    var email: String = "",
    var cep: String = "",
    var endereco: String = "",
    var numero: String = "",
    var complemento: String = "",
    var triagem: Triagem = Triagem()
)

data class Triagem(
    var peso: Double = 0.0,
    var altura: Double = 0.0,
    var objetivo: String = "",
    var frequencia: String = "",
    var statusParq: String = "",
    var lesoes: String = ""
)