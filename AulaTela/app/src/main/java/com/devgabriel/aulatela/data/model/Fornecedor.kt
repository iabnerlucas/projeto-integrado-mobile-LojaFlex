package com.devgabriel.aulatela.data.model

data class Fornecedor(
    val id: Long? = null,
    val nomeFantasia: String,
    val razaoSocial: String? = null,
    val cnpj: String? = null,
    val telefone: String? = null,
    val email: String? = null
)
