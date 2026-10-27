package com.lojaflex.backend.controller

import com.lojaflex.backend.model.ClienteFiado
import com.lojaflex.backend.repository.ClienteFiadoRepository
import com.lojaflex.backend.repository.EmpresaRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/fiados")
class ClienteFiadoController(
    private val clienteFiadoRepository: ClienteFiadoRepository,
    private val empresaRepository: EmpresaRepository
) {

    @GetMapping
    fun getFiados(): ResponseEntity<List<ClienteFiado>> {
        val fiados = clienteFiadoRepository.findByEmpresaId(1L)
        return ResponseEntity.ok(fiados)
    }

    @PostMapping
    fun salvarFiado(@RequestBody fiado: ClienteFiado): ResponseEntity<ClienteFiado> {
        val empresa = empresaRepository.findById(1L).orElse(null)
        fiado.empresa = empresa
        val salvo = clienteFiadoRepository.save(fiado)
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }

    @PutMapping("/{id}")
    fun atualizarFiado(@PathVariable id: Long, @RequestBody fiado: ClienteFiado): ResponseEntity<ClienteFiado> {
        val existente = clienteFiadoRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        existente.nomeCliente = fiado.nomeCliente
        existente.cpfCliente = fiado.cpfCliente
        existente.telefoneWhatsapp = fiado.telefoneWhatsapp
        existente.limiteCredito = fiado.limiteCredito
        existente.valorDevido = fiado.valorDevido
        existente.itensComprados = fiado.itensComprados
        existente.dataCompra = fiado.dataCompra
        existente.horaCompra = fiado.horaCompra
        existente.dataVencimento = fiado.dataVencimento
        existente.pago = fiado.pago

        val atualizado = clienteFiadoRepository.save(existente)
        return ResponseEntity.ok(atualizado)
    }

    @DeleteMapping("/{id}")
    fun deletarFiado(@PathVariable id: Long): ResponseEntity<Void> {
        clienteFiadoRepository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
