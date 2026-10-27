package com.devgabriel.aulatela.data.model

import com.google.gson.annotations.SerializedName

data class Produto(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("nome")
    val nome: String,

    @SerializedName("codigoBarras")
    val codigoBarras: String? = null,

    @SerializedName("preco")
    val preco: Double,

    @SerializedName("quantidadeEstoque", alternate = ["quantidade", "estoque"])
    var quantidadeEstoque: Int = 0,

    @SerializedName("estoqueMinimo")
    val estoqueMinimo: Int = 5,

    @SerializedName("empresaId")
    val empresaId: Long? = null
)