package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class ItemPedidoRequest(
    @SerializedName("produtoId")
    val produtoId: Long,

    @SerializedName("quantidade")
    val quantidade: Int
)