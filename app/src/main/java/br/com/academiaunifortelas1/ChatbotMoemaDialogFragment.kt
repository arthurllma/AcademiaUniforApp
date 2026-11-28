package br.com.academiaunifortelas1

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class ChatbotMoemaDialogFragment : DialogFragment() {

    data class ChatMensagem(
        val texto: String,
        val isMoema: Boolean,
        val isTyping: Boolean = false
    )

    private val mensagens = mutableListOf<ChatMensagem>()
    private lateinit var adapter: ChatAdapter
    private var chatSession: com.google.firebase.ai.Chat? = null
    private var generativeModel: GenerativeModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_chatbot_moema, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvMensagens = view.findViewById<RecyclerView>(R.id.rvChatMensagens)
        val etInput = view.findViewById<EditText>(R.id.etChatInput)
        val btnEnviar = view.findViewById<View>(R.id.btnEnviarChat)
        val btnFechar = view.findViewById<View>(R.id.btnFecharChat)

        adapter = ChatAdapter(mensagens)
        rvMensagens.layoutManager = LinearLayoutManager(requireContext())
        rvMensagens.adapter = adapter

        // Mensagem de boas-vindas inicial da Moema
        if (mensagens.isEmpty()) {
            mensagens.add(
                ChatMensagem(
                    texto = "Olá! Sou a Moema. Posso te ajudar com a execução correta, os músculos trabalhados ou dicas de postura de qualquer exercício. Sobre qual movimento você quer tirar dúvidas agora?",
                    isMoema = true
                )
            )
            adapter.notifyItemInserted(mensagens.size - 1)
        }

        btnFechar.setOnClickListener { dismiss() }

        fun enviarMensagem() {
            val textoPrompt = etInput.text.toString().trim()
            if (textoPrompt.isEmpty()) return

            // Adiciona mensagem do aluno
            mensagens.add(ChatMensagem(texto = textoPrompt, isMoema = false))
            adapter.notifyItemInserted(mensagens.size - 1)
            etInput.setText("")
            rvMensagens.smoothScrollToPosition(mensagens.size - 1)

            // Adiciona indicador "Moema está digitando..."
            val posTyping = mensagens.size
            mensagens.add(ChatMensagem(texto = "Moema está digitando...", isMoema = true, isTyping = true))
            adapter.notifyItemInserted(posTyping)
            rvMensagens.smoothScrollToPosition(posTyping)

            lifecycleScope.launch {
                try {
                    val response = chatSession?.sendMessage(textoPrompt)
                    val respostaTexto = response?.text

                    // Remove indicador de digitação
                    if (posTyping < mensagens.size && mensagens[posTyping].isTyping) {
                        mensagens.removeAt(posTyping)
                        adapter.notifyItemRemoved(posTyping)
                    }

                    val msgFinal = if (!respostaTexto.isNullOrBlank()) {
                        respostaTexto
                    } else {
                        "Ops! Não consegui entender a resposta. Poderia reformular a dúvida?"
                    }

                    mensagens.add(ChatMensagem(texto = msgFinal, isMoema = true))
                    adapter.notifyItemInserted(mensagens.size - 1)
                    rvMensagens.smoothScrollToPosition(mensagens.size - 1)

                } catch (e: Exception) {
                    // Remove indicador de digitação se ainda estiver presente
                    val typingIndex = mensagens.indexOfFirst { it.isTyping }
                    if (typingIndex != -1) {
                        mensagens.removeAt(typingIndex)
                        adapter.notifyItemRemoved(typingIndex)
                    }

                    mensagens.add(
                        ChatMensagem(
                            texto = "Ops! Não consegui me conectar no momento. Tente novamente em instantes.",
                            isMoema = true
                        )
                    )
                    adapter.notifyItemInserted(mensagens.size - 1)
                    rvMensagens.smoothScrollToPosition(mensagens.size - 1)
                }
            }
        }

        btnEnviar.setOnClickListener { enviarMensagem() }

        etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviarMensagem()
                true
            } else {
                false
            }
        }

        carregarContextoETIniciarGemini()
    }

    private fun carregarContextoETIniciarGemini() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
            ?: requireActivity().getSharedPreferences("UsuarioPref", android.content.Context.MODE_PRIVATE).getString("user_id", "") ?: ""

        if (uid.isNotEmpty()) {
            FirebaseFirestore.getInstance().collection("usuarios").document(uid).get()
                .addOnSuccessListener { doc ->
                    val nome = doc.getString("nome")
                        ?: requireActivity().getSharedPreferences("UsuarioPref", android.content.Context.MODE_PRIVATE).getString("primeiro_nome", "Aluno") ?: "Aluno"

                    val triagem = doc.get("triagem") as? Map<*, *>

                    val objetivo = (triagem?.get("objetivo") as? String)
                        ?: doc.getString("objetivo") ?: "Não informado"

                    val lesoes = (triagem?.get("lesoes") as? String)
                        ?: doc.getString("lesoes") ?: doc.getString("lesao") ?: "Nenhuma"

                    val statusParq = (triagem?.get("statusParq") as? String)
                        ?: doc.getString("statusParq") ?: doc.getString("parqStatus") ?: "Sem restrições"

                    inicializarGemini(nome, objetivo, lesoes, statusParq)
                }
                .addOnFailureListener {
                    inicializarGemini("Aluno", "Geral", "Nenhuma", "Sem restrições")
                }
        } else {
            inicializarGemini("Aluno", "Geral", "Nenhuma", "Sem restrições")
        }
    }

    private fun inicializarGemini(nome: String, objetivo: String, lesoes: String, statusParq: String) {
        val systemInstructionText = """
            Você é a Moema, a assistente virtual amigável, objetiva e segura da Academia UNIFOR.
            Seu objetivo é ajudar os alunos com a execução correta dos exercícios, músculos trabalhados e dicas de postura.
            Responda sempre em português de forma clara, acolhedora e direta.
            
            Contexto do Aluno Atual:
            - Nome: $nome
            - Objetivo Principal: $objetivo
            - Restrições Médicas/Lesões: $lesoes
            - Alertas do PAR-Q: $statusParq
            
            Diretrizes Importantes:
            Se o aluno possuir alguma lesão ou alerta do PAR-Q registrado, adapte sempre as suas instruções de postura e execução para prevenir agravamento dessas restrições e recomende os devidos cuidados.
        """.trimIndent()

        try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = "gemini-2.5-flash",
                systemInstruction = content { text(systemInstructionText) }
            )
            generativeModel = model
            chatSession = model.startChat()
        } catch (e: Exception) {
            // Em caso de erro na inicializacao
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                (resources.displayMetrics.widthPixels * 0.90).toInt(),
                (resources.displayMetrics.heightPixels * 0.85).toInt()
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    private class ChatAdapter(private val lista: List<ChatMensagem>) :
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val llMoema: View = itemView.findViewById(R.id.llContainerMoema)
            val tvMoema: TextView = itemView.findViewById(R.id.tvMensagemMoema)
            val llAluno: View = itemView.findViewById(R.id.llContainerAluno)
            val tvAluno: TextView = itemView.findViewById(R.id.tvMensagemAluno)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_mensagem, parent, false)
            return ChatViewHolder(view)
        }

        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
            val item = lista[position]
            if (item.isMoema) {
                holder.llMoema.visibility = View.VISIBLE
                holder.llAluno.visibility = View.GONE
                holder.tvMoema.text = item.texto
                if (item.isTyping) {
                    holder.tvMoema.setTypeface(null, android.graphics.Typeface.ITALIC)
                    holder.tvMoema.setTextColor(Color.parseColor("#64748B"))
                } else {
                    holder.tvMoema.setTypeface(null, android.graphics.Typeface.NORMAL)
                    holder.tvMoema.setTextColor(Color.parseColor("#17218B"))
                }
            } else {
                holder.llMoema.visibility = View.GONE
                holder.llAluno.visibility = View.VISIBLE
                holder.tvAluno.text = item.texto
            }
        }

        override fun getItemCount(): Int = lista.size
    }
}
