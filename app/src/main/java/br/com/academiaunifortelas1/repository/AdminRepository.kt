package br.com.academiaunifortelas1.repository

import br.com.academiaunifortelas1.model.FuncionarioModel

object AdminRepository {
    val funcionariosAtivos = mutableListOf(
        FuncionarioModel(id = "1", nome = "Yuri Silva", cargo = "Administrador", matricula = "98765", email = "yuri.silva@email.com", status = "Ativo"),
        FuncionarioModel(id = "2", nome = "Ana Clara", cargo = "Instrutora", matricula = "98766", email = "ana.clara@email.com", status = "Ativo"),
        FuncionarioModel(id = "3", nome = "Marcos Diaz", cargo = "Instrutor", matricula = "98767", email = "marcos.diaz@email.com", status = "Ativo"),
        FuncionarioModel(id = "4", nome = "Paula Santos", cargo = "Recepcionista", matricula = "98768", email = "paula.santos@email.com", status = "Ativo"),
        FuncionarioModel(id = "5", nome = "Ricardo Alves", cargo = "Instrutor", matricula = "98769", email = "ricardo.alves@email.com", status = "Ativo")
    )

    val cadastrosPendentes = mutableListOf(
        FuncionarioModel(id = "p1", nome = "Camila Rocha", cargo = "Instrutora", cref = "023456-G/CE", email = "camila.rocha@email.com", status = "Pendente"),
        FuncionarioModel(id = "p2", nome = "Bruno Costa", cargo = "Instrutor", cref = "034567-G/CE", email = "bruno.costa@email.com", status = "Pendente"),
        FuncionarioModel(id = "p3", nome = "Juliana Lima", cargo = "Recepcionista", setor = "Atendimento", email = "juliana.lima@email.com", status = "Pendente")
    )

    fun aprovarCadastro(id: String) {
        val item = cadastrosPendentes.find { it.id == id }
        if (item != null) {
            cadastrosPendentes.remove(item)
            item.status = "Ativo"
            if (item.matricula.isEmpty()) {
                item.matricula = (98770 + funcionariosAtivos.size).toString()
            }
            funcionariosAtivos.add(item)
        }
    }

    fun rejeitarCadastro(id: String) {
        cadastrosPendentes.removeAll { it.id == id }
    }
}
