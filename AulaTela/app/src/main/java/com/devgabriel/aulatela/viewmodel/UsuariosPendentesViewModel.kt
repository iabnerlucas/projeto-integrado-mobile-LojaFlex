package com.devgabriel.aulatela.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.model.UsuarioResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UsuariosPendentesState {
    object Idle : UsuariosPendentesState()
    object Loading : UsuariosPendentesState()
    data class Success(val usuarios: List<UsuarioResponse>) : UsuariosPendentesState()
    data class Error(val message: String) : UsuariosPendentesState()
    data class ActionSuccess(val message: String) : UsuariosPendentesState()
}

class UsuariosPendentesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UsuariosPendentesState>(UsuariosPendentesState.Idle)
    val uiState: StateFlow<UsuariosPendentesState> = _uiState.asStateFlow()

    fun carregarPendentes(empresaId: Long) {
        viewModelScope.launch {
            _uiState.value = UsuariosPendentesState.Loading
            try {
                val response = RetrofitClient.apiService.listarUsuariosPendentes(empresaId)
                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    val mapeada = lista.map {
                        UsuarioResponse(
                            id = it.id ?: 0L,
                            nome = it.nome ?: "Sem nome",
                            login = it.email ?: "Sem email",
                            perfil = it.perfil?.name ?: "OPERADOR",
                            statusAprovacao = it.statusAprovacao ?: "PENDENTE"
                        )
                    }
                    _uiState.value = UsuariosPendentesState.Success(mapeada)
                } else {
                    _uiState.value = UsuariosPendentesState.Error("Erro ao carregar pendentes: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = UsuariosPendentesState.Error("Falha na conexão: ${e.message}")
            }
        }
    }

    fun aprovarUsuario(id: Long, empresaId: Long) {
        viewModelScope.launch {
            _uiState.value = UsuariosPendentesState.Loading
            try {
                val response = RetrofitClient.apiService.aprovarUsuario(id)
                if (response.isSuccessful) {
                    _uiState.value = UsuariosPendentesState.ActionSuccess("Usuário aprovado com sucesso!")
                    carregarPendentes(empresaId)
                } else {
                    _uiState.value = UsuariosPendentesState.Error("Erro ao aprovar: ${response.code()}")
                    carregarPendentes(empresaId) // Recarrega mesmo assim
                }
            } catch (e: Exception) {
                _uiState.value = UsuariosPendentesState.Error("Erro na aprovação: ${e.message}")
                carregarPendentes(empresaId)
            }
        }
    }

    fun rejeitarUsuario(id: Long, empresaId: Long) {
        viewModelScope.launch {
            _uiState.value = UsuariosPendentesState.Loading
            try {
                val response = RetrofitClient.apiService.rejeitarUsuario(id)
                if (response.isSuccessful) {
                    _uiState.value = UsuariosPendentesState.ActionSuccess("Usuário rejeitado.")
                    carregarPendentes(empresaId)
                } else {
                    _uiState.value = UsuariosPendentesState.Error("Erro ao rejeitar: ${response.code()}")
                    carregarPendentes(empresaId)
                }
            } catch (e: Exception) {
                _uiState.value = UsuariosPendentesState.Error("Erro na rejeição: ${e.message}")
                carregarPendentes(empresaId)
            }
        }
    }
}
