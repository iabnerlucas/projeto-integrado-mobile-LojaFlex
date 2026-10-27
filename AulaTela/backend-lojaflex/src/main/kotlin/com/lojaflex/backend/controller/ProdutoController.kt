package com.lojaflex.backend.controller

import com.lojaflex.backend.model.Produto
import com.lojaflex.backend.repository.EmpresaRepository
import com.lojaflex.backend.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/produtos")
class ProdutoController(
    private val produtoRepository: ProdutoRepository,
    private val empresaRepository: EmpresaRepository
) {

    @GetMapping
    fun getProdutos(@RequestParam(required = false) busca: String?): ResponseEntity<List<Produto>> {
        val empresaId = 1L
        val produtos = if (busca.isNullOrBlank()) {
            produtoRepository.findByEmpresaId(empresaId)
        } else {
            produtoRepository.findByEmpresaIdAndNomeContainingIgnoreCase(empresaId, busca)
        }
        return ResponseEntity.ok(produtos)
    }

    @PostMapping
    fun salvarProduto(@RequestBody produto: Produto): ResponseEntity<Produto> {
        val empresa = empresaRepository.findById(1L).orElse(null)
        produto.empresa = empresa
        val salvo = produtoRepository.save(produto)
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }

    @PutMapping("/{id}")
    fun editarProduto(@PathVariable id: Long, @RequestBody produto: Produto): ResponseEntity<Produto> {
        val existente = produtoRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        existente.nome = produto.nome
        existente.preco = produto.preco
        existente.precoCusto = produto.precoCusto
        existente.codigoBarra = produto.codigoBarra
        existente.quantidadeEstoque = produto.quantidadeEstoque

        val atualizado = produtoRepository.save(existente)
        return ResponseEntity.ok(atualizado)
    }

    @DeleteMapping("/{id}")
    fun deletarProduto(@PathVariable id: Long): ResponseEntity<Void> {
        produtoRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
