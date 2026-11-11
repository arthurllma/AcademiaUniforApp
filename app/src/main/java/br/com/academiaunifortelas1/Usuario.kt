package br.com.academiaunifortelas1

/**
 * Representa um Aluno/Usuário no Cloud Firestore.
 * 
 * NOTA: Para que o Firestore consiga converter os documentos do banco de dados 
 * diretamente para esta classe (deserialização), é obrigatório que ela tenha um 
 * "construtor vazio" (no-argument constructor).
 * No Kotlin, nós alcançamos isso definindo valores padrão para todas as propriedades.
 */
data class Usuario(
    var id: String = "",
    var nome: String = "",
    var email: String = "",
    var cpf: String = "",
    var triagem: Triagem = Triagem()
)

/**
 * Dados médicos e físicos preenchidos durante o fluxo de triagem.
 */
data class Triagem(
    var peso: Double = 0.0,
    var altura: Double = 0.0,
    var objetivo: String = "",
    var frequencia: String = "",
    var statusParq: String = "",
    var lesoes: String = ""
)
