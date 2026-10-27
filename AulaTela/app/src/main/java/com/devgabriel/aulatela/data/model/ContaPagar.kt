package com.devgabriel.aulatela.data.model

data class ContaPagar(
    val id: Long? = null,
    val descricao: String,
    val valor: Double,
    val dataVencimento: String,
    var paga: Boolean = false
)
