package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class ClienteFiado(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("telefone")
    val telefone: String = "",

    @SerializedName("valorDivida", alternate = ["valorDevido", "saldoDevedor", "valor"])
    var valorDivida: Double = 0.0,

    @SerializedName("dataVencimento")
    val dataVencimento: String = "",

    @SerializedName("status")
    var status: String = "PENDENTE",

    @SerializedName("empresaId")
    val empresaId: Long? = null
)