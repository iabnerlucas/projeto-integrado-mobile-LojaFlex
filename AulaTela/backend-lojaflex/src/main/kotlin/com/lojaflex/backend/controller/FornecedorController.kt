package com.lojaflex.backend.controller

import com.lojaflex.backend.model.Fornecedor
import com.lojaflex.backend.repository.EmpresaRepository
import com.lojaflex.backend.repository.FornecedorRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/fornecedores")
class FornecedorController(
    private val fornecedorRepository: FornecedorRepository,
    private val empresaRepository: EmpresaRepository
) {

    @GetMapping
    fun getFornecedores(): ResponseEntity<List<Fornecedor>> {
        val fornecedores = fornecedorRepository.findByEmpresaId(1L)
        return ResponseEntity.ok(fornecedores)
    }

    @PostMapping
    fun salvarFornecedor(@RequestBody fornecedor: Fornecedor): ResponseEntity<Fornecedor> {
        val empresa = empresaRepository.findById(1L).orElse(null)
        fornecedor.empresa = empresa
        val salvo = fornecedorRepository.save(fornecedor)
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }

    @DeleteMapping("/{id}")
    fun deletarFornecedor(@PathVariable id: Long): ResponseEntity<Void> {
        fornecedorRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
