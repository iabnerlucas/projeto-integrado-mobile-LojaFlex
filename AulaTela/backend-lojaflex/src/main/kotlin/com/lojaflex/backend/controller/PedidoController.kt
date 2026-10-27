package com.lojaflex.backend.controller

import com.lojaflex.backend.dto.PedidoRequest
import com.lojaflex.backend.dto.PedidoResponse
import com.lojaflex.backend.model.ItemPedido
import com.lojaflex.backend.model.Pedido
import com.lojaflex.backend.repository.EmpresaRepository
import com.lojaflex.backend.repository.PedidoRepository
import com.lojaflex.backend.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/pedidos")
class PedidoController(
    private val pedidoRepository: PedidoRepository,
    private val produtoRepository: ProdutoRepository,
    private val empresaRepository: EmpresaRepository
) {

    @PostMapping
    @Transactional
    fun criarPedido(@RequestBody request: PedidoRequest): ResponseEntity<PedidoResponse> {
        val empresa = empresaRepository.findById(1L).orElse(null)

        val pedido = Pedido(
            formaPagamento = request.formaPagamento,
            valorTotal = request.valorTotal,
            valorRecebido = request.valorRecebido,
            troco = request.troco,
            empresa = empresa
        )

        val itens = request.itens.mapNotNull { itemReq ->
            val produto = produtoRepository.findById(itemReq.produtoId).orElse(null)
            if (produto != null) {
                // Dar baixa automática no estoque do produto no MySQL
                val novoEstoque = (produto.quantidadeEstoque - itemReq.quantidade).coerceAtLeast(0)
                produto.quantidadeEstoque = novoEstoque
                produtoRepository.save(produto)

                ItemPedido(
                    pedido = pedido,
                    produto = produto,
                    quantidade = itemReq.quantidade,
                    precoUnitario = itemReq.precoUnitario
                )
            } else null
        }.toMutableList()

        pedido.itens.addAll(itens)
        val salvo = pedidoRepository.save(pedido)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            PedidoResponse(
                id = salvo.id!!,
                formaPagamento = salvo.formaPagamento,
                valorTotal = salvo.valorTotal,
                valorRecebido = salvo.valorRecebido,
                troco = salvo.troco,
                mensagem = "Venda registrada no banco de dados e baixa dada no estoque!"
            )
        )
    }
}
