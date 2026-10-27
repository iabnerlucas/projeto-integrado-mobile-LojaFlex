package com.lojaflex.backend.controller

import com.lojaflex.backend.model.ContaPagar
import com.lojaflex.backend.repository.ContaPagarRepository
import com.lojaflex.backend.repository.EmpresaRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/contas-pagar")
class ContaPagarController(
    private val contaPagarRepository: ContaPagarRepository,
    private val empresaRepository: EmpresaRepository
) {

    @GetMapping
    fun getContasPagar(): ResponseEntity<List<ContaPagar>> {
        val contas = contaPagarRepository.findByEmpresaId(1L)
        return ResponseEntity.ok(contas)
    }

    @PostMapping
    fun salvarContaPagar(@RequestBody conta: ContaPagar): ResponseEntity<ContaPagar> {
        val empresa = empresaRepository.findById(1L).orElse(null)
        conta.empresa = empresa
        val salva = contaPagarRepository.save(conta)
        return ResponseEntity.status(HttpStatus.CREATED).body(salva)
    }

    @PutMapping("/{id}/pagar")
    fun pagarConta(@PathVariable id: Long): ResponseEntity<ContaPagar> {
        val conta = contaPagarRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        conta.paga = true
        val atualizada = contaPagarRepository.save(conta)
        return ResponseEntity.ok(atualizada)
    }
}
