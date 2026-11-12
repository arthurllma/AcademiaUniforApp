package br.com.academiaunifortelas1.viewmodel

import androidx.lifecycle.ViewModel
import br.com.academiaunifortelas1.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UsuarioViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    
    // Estado interno mutável
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    // Estado exposto e imutável para a View (Compose) observar
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    init {
        // Ao inicializar o ViewModel, começamos a observar os dados do Firestore
        observarUsuarios()
    }

    private fun observarUsuarios() {
        // ATENÇÃO: Substitua "usuarios" pelo nome real da sua coleção no Firestore
        db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Tratar o erro de leitura, se houver
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Converte os documentos do Firestore para a nossa Data Class (Usuario)
                    val listaDeUsuarios = snapshot.documents.mapNotNull { documento ->
                        documento.toObject(Usuario::class.java)
                    }
                    // Atualiza a lista que a interface está observando
                    _usuarios.value = listaDeUsuarios
                }
            }
    }

    // Como escrever um novo dado no Firestore
    fun adicionarUsuario(usuario: Usuario) {
        db.collection("usuarios").add(usuario)
            .addOnSuccessListener {
                // Sucesso ao adicionar
            }
            .addOnFailureListener { e ->
                // Falha ao adicionar
            }
    }
}
