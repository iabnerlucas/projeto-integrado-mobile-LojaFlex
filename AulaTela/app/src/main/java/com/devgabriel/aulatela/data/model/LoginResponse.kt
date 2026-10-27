
package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String? = null,

    @SerializedName("usuario")
    val usuario: UsuarioLogin? = null
)

data class UsuarioLogin(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nome")
    val nome: String? = null,

    @SerializedName("login")
    val login: String? = null,

    @SerializedName("perfil")
    val perfil: String? = null,

    @SerializedName("statusAprovacao")
    val statusAprovacao: String? = null,

    @SerializedName("empresa")
    val empresa: EmpresaLogin? = null
)

data class EmpresaLogin(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nomeFantasia")
    val nomeFantasia: String? = null,

    @SerializedName("statusAprovacao")
    val statusAprovacao: String? = null
)
