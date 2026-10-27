package com.lojaflex.backend.controller

import com.lojaflex.backend.dto.DashboardMetricas
import com.lojaflex.backend.repository.PedidoRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/dashboard")
class DashboardController(
    private val pedidoRepository: PedidoRepository
) {

    @GetMapping("/metricas")
    fun getMetricasDashboard(): ResponseEntity<DashboardMetricas> {
        val pedidos = pedidoRepository.findByEmpresaId(1L)

        val totalFaturado = pedidos.sumOf { it.valorTotal }
        val totalVendasCount = pedidos.size
        val ticketMedio = if (totalVendasCount > 0) totalFaturado / totalVendasCount else 0.0

        val faturamentoDinheiro = pedidos.filter { it.formaPagamento.contains("DINHEIRO", ignoreCase = true) }.sumOf { it.valorTotal }
        val faturamentoPix = pedidos.filter { it.formaPagamento.contains("PIX", ignoreCase = true) }.sumOf { it.valorTotal }
        val faturamentoDebito = pedidos.filter { it.formaPagamento.contains("DÉBITO", ignoreCase = true) || it.formaPagamento.contains("DEBITO", ignoreCase = true) }.sumOf { it.valorTotal }
        val faturamentoCredito = pedidos.filter { it.formaPagamento.contains("CRÉDITO", ignoreCase = true) || it.formaPagamento.contains("CREDITO", ignoreCase = true) }.sumOf { it.valorTotal }
        val faturamentoFiado = pedidos.filter { it.formaPagamento.contains("FIADO", ignoreCase = true) }.sumOf { it.valorTotal }

        val metricas = DashboardMetricas(
            totalFaturado = totalFaturado,
            totalVendasCount = totalVendasCount,
            ticketMedio = ticketMedio,
            faturamentoDinheiro = faturamentoDinheiro,
            faturamentoPix = faturamentoPix,
            faturamentoDebito = faturamentoDebito,
            faturamentoCredito = faturamentoCredito,
            faturamentoFiado = faturamentoFiado
        )

        return ResponseEntity.ok(metricas)
    }
}
