package br.com.academiaunifortelas1.repository

object TriagemDataManager {
    var experiencia: String = ""
    var objetivo: String = ""
    var frequencia: String = ""

    var peso: String = ""
    var altura: String = ""
    var imc: String = ""
    var presencialBiometria: Boolean = false

    var swQ1: Boolean = false
    var swQ2: Boolean = false
    var swQ3: Boolean = false
    var swQ4: Boolean = false
    var swQ5: Boolean = false
    var statusParq: String = "Sem restrições"
    var parqRestricoes: Boolean = false

    var lesoes: String = ""
    var esportes: String = ""
    var observacoes: String = ""

    var termoAceito: Boolean = false

    fun reset() {
        experiencia = ""
        objetivo = ""
        frequencia = ""
        peso = ""
        altura = ""
        imc = ""
        presencialBiometria = false
        swQ1 = false
        swQ2 = false
        swQ3 = false
        swQ4 = false
        swQ5 = false
        statusParq = "Sem restrições"
        parqRestricoes = false
        lesoes = ""
        esportes = ""
        observacoes = ""
        termoAceito = false
    }
}
