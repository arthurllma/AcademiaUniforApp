package br.com.academiaunifortelas1.repository

import br.com.academiaunifortelas1.R
import br.com.academiaunifortelas1.model.ExercicioModel

object ExerciciosRepository {
    val listaExercicios = mutableListOf(
        ExercicioModel(
            id = "1",
            nome = "Supino Reto",
            grupoMuscular = "Peito",
            observacoes = "Manter as escápulas apoiadas e controlar a descida.",
            videoUrl = "https://www.youtube.com/watch?v=rT7DgCr-3pg",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "2",
            nome = "Supino Inclinado",
            grupoMuscular = "Peito",
            observacoes = "Ajustar o banco a 30 graus e focar no peitoral superior.",
            videoUrl = "https://www.youtube.com/watch?v=0G2_XV7slIg",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "3",
            nome = "Crucifixo",
            grupoMuscular = "Peito",
            observacoes = "Manter os cotovelos levemente flexionados durante a abertura.",
            videoUrl = "https://www.youtube.com/watch?v=eozdVDA78K0",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "4",
            nome = "Tríceps Pulley",
            grupoMuscular = "Tríceps",
            observacoes = "Manter cotovelos colados ao corpo e estender completamente.",
            videoUrl = "https://www.youtube.com/watch?v=vB5OHsJ3EME",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "5",
            nome = "Tríceps Francês",
            grupoMuscular = "Tríceps",
            observacoes = "Controlar a descida atrás da cabeça com amplitude total.",
            videoUrl = "https://www.youtube.com/watch?v=_gsUck-7M74",
            imageResId = R.drawable.ic_academia_logo
        )
    )

    fun buscarPorNome(nome: String): ExercicioModel? {
        return listaExercicios.find { it.nome.equals(nome, ignoreCase = true) }
    }
}
