package com.lojaflex.backend.dto

import com.lojaflex.backend.model.PerfilAcesso

data class LoginRequest(
    val email: String,
    val senha: String
)

data class UsuarioResponseDTO(
    val id: Long,
    val nome: String,
    val email: String,
    val perfil: PerfilAcesso,
    val empresaId: Long?,
    val nomeEmpresa: String?
)

data class LoginResponse(
    val token: String,
    val usuario: UsuarioResponseDTO
)

data class CadastroRequest(
    val nome: String,
    val email: String,
    val senha: String,
    val perfil: String? = "CAIXA",
    val empresaId: Long? = 1L
)

data class CadastroEmpresaRequest(
    val id: Long? = null,
    val cnpjCpf: String,
    val nomeFantasia: String,
    val telefone: String?,
    val nomeAdmin: String,
    val emailAdmin: String,
    val senhaAdmin: String?
)

data class AprovarLojaRequest(
    val emailGerente: String,
    val senhaGerente: String
)
