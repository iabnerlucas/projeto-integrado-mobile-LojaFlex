package com.lojaflex.backend.dto

data class ItemPedidoRequest(
    val produtoId: Long,
    val quantidade: Int,
    val precoUnitario: Double
)

data class PedidoRequest(
    val formaPagamento: String,
    val valorTotal: Double,
    val valorRecebido: Double,
    val troco: Double,
    val itens: List<ItemPedidoRequest>
)

data class PedidoResponse(
    val id: Long,
    val formaPagamento: String,
    val valorTotal: Double,
    val valorRecebido: Double,
    val troco: Double,
    val mensagem: String = "Venda finalizada com sucesso!"
)
