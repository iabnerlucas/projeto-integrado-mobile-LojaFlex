package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class PedidoResponse(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("valorTotal")
    val valorTotal: Double? = null,

    @SerializedName("formaPagamento")
    val formaPagamento: String? = null,

    @SerializedName("dataHora")
    val dataHora: String? = null,

    @SerializedName("status")
    val status: String? = null
)