package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class PedidoRequest(
    @SerializedName("empresaId")
    val empresaId: Long? = null,

    @SerializedName("usuarioId")
    val usuarioId: Long? = null,

    @SerializedName("formaPagamento")
    val formaPagamento: String = "DINHEIRO",

    @SerializedName("itens")
    val itens: List<ItemPedidoRequest>
)