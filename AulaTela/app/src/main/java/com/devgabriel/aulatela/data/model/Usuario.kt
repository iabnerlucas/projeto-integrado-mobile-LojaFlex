package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

enum class PerfilEnum {
    @SerializedName("SUPORTE") SUPORTE,
    @SerializedName("GERENTE_LOJA") GERENTE_LOJA,
    @SerializedName("OPERADOR") OPERADOR
}

data class Usuario(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nome") val nome: String? = null,
    @SerializedName("login") val login: String? = null, // Essencial aqui também
    @SerializedName("email") val email: String? = null,
    @SerializedName("perfil") val perfil: PerfilEnum? = null,
    @SerializedName("empresaId") val empresaId: Long? = null,
    @SerializedName("nomeEmpresa") val nomeEmpresa: String? = null,
    @SerializedName("statusAprovacao") val statusAprovacao: String? = null
)

data class UsuarioResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("nome") val nome: String,
    @SerializedName("login") val login: String,
    @SerializedName("perfil") val perfil: String,
    @SerializedName("statusAprovacao") val statusAprovacao: String
)

data class CadastroRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("login") val login: String, // Ajustado caso precise enviar login em vez de email
    @SerializedName("perfil") val perfil: String,
    @SerializedName("senha") val senha: String
)