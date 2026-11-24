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
        ),
        // Treino B - Pernas e Glúteos
        ExercicioModel(
            id = "6",
            nome = "Leg Press 45",
            grupoMuscular = "Pernas",
            observacoes = "Manter os pés alinhados com a largura dos ombros e não travar os joelhos.",
            videoUrl = "https://www.youtube.com/watch?v=IZxyjW7MPJQ",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "7",
            nome = "Extensor de Pernas",
            grupoMuscular = "Pernas",
            observacoes = "Ajustar o encosto para apoiar bem a coluna e focar no quadríceps.",
            videoUrl = "https://www.youtube.com/watch?v=YyvSfVjQeL0",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "8",
            nome = "Flexora Sentada",
            grupoMuscular = "Pernas",
            observacoes = "Manter o quadril bem apoiado no banco durante o movimento.",
            videoUrl = "https://www.youtube.com/watch?v=F488k67O3NQ",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "9",
            nome = "Adutora",
            grupoMuscular = "Pernas",
            observacoes = "Controlar a abertura e juntar as pernas de forma cadenciada.",
            videoUrl = "https://www.youtube.com/watch?v=qCInN3z6gV8",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "10",
            nome = "Abdutora",
            grupoMuscular = "Glúteos",
            observacoes = "Manter a postura ereta e forçar a abertura contra a resistência.",
            videoUrl = "https://www.youtube.com/watch?v=G_8L_Q23LdQ",
            imageResId = R.drawable.ic_academia_logo
        ),
        // Treino C - Costas e Bíceps
        ExercicioModel(
            id = "11",
            nome = "Puxada Frontal",
            grupoMuscular = "Costas",
            observacoes = "Puxar a barra até a altura do peito estufando o tórax.",
            videoUrl = "https://www.youtube.com/watch?v=CAwf7n6Luuc",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "12",
            nome = "Remada Curvada",
            grupoMuscular = "Costas",
            observacoes = "Manter a coluna ereta e puxar o peso em direção ao umbigo.",
            videoUrl = "https://www.youtube.com/watch?v=6TSP1T2_s2E",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "13",
            nome = "Rosca Direta",
            grupoMuscular = "Bíceps",
            observacoes = "Manter os cotovelos fixos ao lado do tronco sem roubar com o corpo.",
            videoUrl = "https://www.youtube.com/watch?v=in7PaeYlhrM",
            imageResId = R.drawable.ic_academia_logo
        ),
        ExercicioModel(
            id = "14",
            nome = "Rosca Martelo",
            grupoMuscular = "Bíceps",
            observacoes = "Manter a pegada neutra e controlar a descida do halter.",
            videoUrl = "https://www.youtube.com/watch?v=zC3nLlEvinU",
            imageResId = R.drawable.ic_academia_logo
        )
    )

    fun buscarPorNome(nome: String): ExercicioModel? {
        return listaExercicios.find { it.nome.equals(nome, ignoreCase = true) }
    }
}
