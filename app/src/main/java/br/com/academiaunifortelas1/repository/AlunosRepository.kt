package br.com.academiaunifortelas1.repository

import br.com.academiaunifortelas1.model.AlunoModel

object AlunosRepository {
    val listaAlunos = mutableListOf(
        AlunoModel("1", "João Silva", "CPF: ***.***.123-45", "Ficha Ativa"),
        AlunoModel("2", "Maria Santos", "CPF: ***.***.432-11", "Aguardando Ficha"),
        AlunoModel("3", "Lucas Ferreira", "CPF: ***.***.882-00", "Ficha Ativa"),
        AlunoModel("4", "Ana Costa", "CPF: ***.***.112-99", "Ficha Vencida"),
        AlunoModel("5", "Pedro Henrique", "CPF: ***.***.990-12", "Ficha Ativa"),
        AlunoModel("6", "Camila Alves", "CPF: ***.***.771-33", "Ficha Ativa")
    )

    fun getQuantidadeSemFicha(): Int {
        return listaAlunos.count { it.statusFicha == "Aguardando Ficha" || it.statusFicha == "Ficha Vencida" }
    }
}
