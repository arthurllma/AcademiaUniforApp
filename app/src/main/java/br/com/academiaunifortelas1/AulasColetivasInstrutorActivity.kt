package br.com.academiaunifortelas1

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AulasColetivasInstrutorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aulas_coletivas_instrutor)

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        // Mock Item 1
        val item1 = findViewById<View>(R.id.itemAula1)
        item1.findViewById<TextView>(R.id.tvAulaTitulo).text = "Muay Thai Iniciante"
        item1.findViewById<TextView>(R.id.tvAulaDias).text = "Seg e Qua • 07:00"
        item1.findViewById<TextView>(R.id.tvProfessor).text = "Prof. Yuri Silva"
        item1.findViewById<TextView>(R.id.tvAulaStatus).apply {
            text = "Vagas Limitadas"
            setBackgroundResource(R.drawable.bg_badge_red)
            setTextColor(Color.parseColor("#EF4444")) // Red text
        }
        item1.findViewById<TextView>(R.id.tvOcupacao).text = "18/20 Alunos"
        item1.findViewById<ProgressBar>(R.id.pbOcupacao).apply {
            progress = 90
            progressTintList = ColorStateList.valueOf(Color.parseColor("#2558F5"))
        }

        // Mock Item 2
        val item2 = findViewById<View>(R.id.itemAula2)
        item2.findViewById<TextView>(R.id.tvAulaTitulo).text = "Circuito"
        item2.findViewById<TextView>(R.id.tvAulaDias).text = "Seg e Qua • 08:30"
        item2.findViewById<TextView>(R.id.tvProfessor).text = "Profa. Ana Clara"
        item2.findViewById<TextView>(R.id.tvAulaStatus).apply {
            text = "Vagas Disponíveis"
            setBackgroundResource(R.drawable.bg_badge_green)
            setTextColor(Color.parseColor("#16A34A")) // Green text
        }
        item2.findViewById<TextView>(R.id.tvOcupacao).text = "12/20 Alunos"
        item2.findViewById<ProgressBar>(R.id.pbOcupacao).apply {
            progress = 60
            progressTintList = ColorStateList.valueOf(Color.parseColor("#2558F5"))
        }

        // Mock Item 3
        val item3 = findViewById<View>(R.id.itemAula3)
        item3.findViewById<TextView>(R.id.tvAulaTitulo).text = "Pilates"
        item3.findViewById<TextView>(R.id.tvAulaDias).text = "Ter e Qui • 18:00"
        item3.findViewById<TextView>(R.id.tvProfessor).text = "Prof. Yuri Silva"
        item3.findViewById<TextView>(R.id.tvAulaStatus).apply {
            text = "Lotado"
            setBackgroundResource(R.drawable.bg_badge_red)
            setTextColor(Color.parseColor("#EF4444")) // Red text
        }
        item3.findViewById<TextView>(R.id.tvOcupacao).text = "25/25 Alunos"
        item3.findViewById<ProgressBar>(R.id.pbOcupacao).apply {
            progress = 100
            progressTintList = ColorStateList.valueOf(Color.parseColor("#EF4444")) // Red bar
        }

        // Mock Item 4
        val item4 = findViewById<View>(R.id.itemAula4)
        item4.findViewById<TextView>(R.id.tvAulaTitulo).text = "Pilates"
        item4.findViewById<TextView>(R.id.tvAulaDias).text = "Ter e Qui • 19:30"
        item4.findViewById<TextView>(R.id.tvProfessor).text = "Prof. Marcos Diaz"
        item4.findViewById<TextView>(R.id.tvAulaStatus).apply {
            text = "Vagas Disponíveis"
            setBackgroundResource(R.drawable.bg_badge_green)
            setTextColor(Color.parseColor("#16A34A")) // Green text
        }
        item4.findViewById<TextView>(R.id.tvOcupacao).text = "15/20 Alunos"
        item4.findViewById<ProgressBar>(R.id.pbOcupacao).apply {
            progress = 75
            progressTintList = ColorStateList.valueOf(Color.parseColor("#2558F5"))
        }

        // Mock Item 5
        val item5 = findViewById<View>(R.id.itemAula5)
        item5.findViewById<TextView>(R.id.tvAulaTitulo).text = "Pilates Clínico"
        item5.findViewById<TextView>(R.id.tvAulaDias).text = "Sex • 09:00"
        item5.findViewById<TextView>(R.id.tvProfessor).text = "Profa. Julia Costa"
        item5.findViewById<TextView>(R.id.tvAulaStatus).apply {
            text = "Vagas Disponíveis"
            setBackgroundResource(R.drawable.bg_badge_green)
            setTextColor(Color.parseColor("#16A34A")) // Green text
        }
        item5.findViewById<TextView>(R.id.tvOcupacao).text = "8/10 Alunos"
        item5.findViewById<ProgressBar>(R.id.pbOcupacao).apply {
            progress = 80
            progressTintList = ColorStateList.valueOf(Color.parseColor("#2558F5"))
        }
    }
}
