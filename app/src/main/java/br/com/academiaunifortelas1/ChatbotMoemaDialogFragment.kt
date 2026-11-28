package br.com.academiaunifortelas1

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
import kotlinx.coroutines.withTimeout

class ChatbotMoemaDialogFragment : DialogFragment() {

    data class ChatMensagem(
        val texto: String,
        val isMoema: Boolean,
        val isTyping: Boolean = false
    )

    private val mensagens = mutableListOf<ChatMensagem>()
    private lateinit var adapter: ChatAdapter
    private var chatSession: com.google.firebase.ai.Chat? = null
    private var systemInstructionText: String = ""

    private fun criarGenerativeModel(modelName: String): GenerativeModel {
        return Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = modelName,
                systemInstruction = content { text(systemInstructionText) }
            )
    }

    private fun criarChatSession(modelName: String): com.google.firebase.ai.Chat? {
        return try {
            val model = criarGenerativeModel(modelName)
            model.startChat()
        } catch (e: Exception) {
            Log.e("MoemaChat", "Erro ao criar chat session para $modelName", e)
            null
        }
    }

    private fun limparMarkdown(texto: String): String {
        return texto
            .replace("**", "")
            .replace("###", "")
            .replace("---", "")
            .trim()
    }

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
        val cardChatbot = view.findViewById<View>(R.id.cardChatbot)

        cardChatbot?.clipToOutline = true

        val density = resources.displayMetrics.density

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            val sysBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            val bottomPadding = if (imeBottom > 0) {
                imeBottom + (16 * density).toInt()
            } else {
                sysBars.bottom + (32 * density).toInt()
            }

            val topPadding = sysBars.top + (32 * density).toInt()
            val sidePadding = sysBars.left + (20 * density).toInt()

            v.setPadding(
                sidePadding,
                topPadding,
                sidePadding,
                bottomPadding
            )

            if (imeBottom > 0 && mensagens.isNotEmpty()) {
                rvMensagens.post {
                    rvMensagens.smoothScrollToPosition(mensagens.size - 1)
                }
            }
            insets
        }

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
            if (textoPrompt.isEmpty() || !btnEnviar.isEnabled) return

            btnEnviar.isEnabled = false

            // Adiciona mensagem do aluno
            mensagens.add(ChatMensagem(texto = textoPrompt, isMoema = false))
            adapter.notifyItemInserted(mensagens.size - 1)
            etInput.setText("")
            rvMensagens.smoothScrollToPosition(mensagens.size - 1)

            // Adiciona indicador "Moema está digitando..."
            val msgDigitando = ChatMensagem(texto = "Moema está digitando...", isMoema = true, isTyping = true)
            mensagens.add(msgDigitando)
            adapter.notifyItemInserted(mensagens.size - 1)
            rvMensagens.smoothScrollToPosition(mensagens.size - 1)

            lifecycleScope.launch {
                var respostaTexto: String? = null
                try {
                    // Tenta primeiro com o modelo principal (gemini-3.8-flash) com timeout de 15s
                    respostaTexto = withTimeout(15000L) {
                        if (chatSession == null) {
                            chatSession = criarChatSession("gemini-3.8-flash")
                        }
                        chatSession?.sendMessage(textoPrompt)?.text
                    }
                } catch (e: Exception) {
                    Log.w("MoemaChat", "Falha ou timeout no modelo principal (gemini-3.8-flash), tentando fallback...", e)
                    try {
                        // Fallback automático com modelo de reserva (gemini-3.8-flash-lite)
                        val chatSessionReserva = criarChatSession("gemini-3.8-flash-lite")
                        respostaTexto = withTimeout(15000L) {
                            chatSessionReserva?.sendMessage(textoPrompt)?.text
                        }
                    } catch (e2: Exception) {
                        Log.e("MoemaChat", "Falha na IA (modelo principal e fallback)", e2)
                    }
                } finally {
                    btnEnviar.isEnabled = true
                }

                mensagens.remove(msgDigitando)
                adapter.notifyDataSetChanged()

                val msgFinal = if (!respostaTexto.isNullOrBlank()) {
                    limparMarkdown(respostaTexto)
                } else {
                    "Ops! Estou sem conexão no momento. Verifique sua internet e tente novamente."
                }

                mensagens.add(ChatMensagem(texto = msgFinal, isMoema = true))
                adapter.notifyItemInserted(mensagens.size - 1)
                rvMensagens.smoothScrollToPosition(mensagens.size - 1)
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
        systemInstructionText = """
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
            Responda apenas em texto simples, sem usar asteriscos (**), cerquilhas (###) ou traços (---). Use apenas quebras de linha e tópicos simples com bullet (•).
        """.trimIndent()

        chatSession = criarChatSession("gemini-3.8-flash")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
    }

    private class ChatAdapter(private val lista: List<ChatMensagem>) :
        RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

        class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val llMoema: View = itemView.findViewById(R.id.llContainerMoema)
            val ivIconMoema: ImageView = itemView.findViewById(R.id.ivIconMoema)
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
                holder.ivIconMoema.setImageResource(R.drawable.icone_moema)
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
