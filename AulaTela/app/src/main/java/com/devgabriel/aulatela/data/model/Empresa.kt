package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class Empresa(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("cnpjCpf") val cnpjCpf: String? = null,
    @SerializedName("nomeFantasia") val nomeFantasia: String? = null,
    @SerializedName("telefone") val telefone: String? = null,
    @SerializedName("emailAdmin") val emailAdmin: String? = null
)

data class CadastroEmpresaRequest(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("cnpjCpf") val cnpjCpf: String,
    @SerializedName("nomeFantasia") val nomeFantasia: String,
    @SerializedName("telefone") val telefone: String? = null,
    @SerializedName("nomeAdmin") val nomeAdmin: String,
    @SerializedName("emailAdmin") val emailAdmin: String,
    @SerializedName("senhaAdmin") val senhaAdmin: String? = null
)

data class AprovarLojaRequest(
    @SerializedName("emailGerente") val emailGerente: String,
    @SerializedName("senhaGerente") val senhaGerente: String
)
