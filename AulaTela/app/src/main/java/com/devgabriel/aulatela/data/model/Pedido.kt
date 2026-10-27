package com.devgabriel.aulatela.data.model

data class ItemCarrinho(
    val produto: Produto,
    var quantidade: Int
) {
    val subtotal: Double
        get() = produto.preco * quantidade
}
