package com.devgabriel.aulatela.data.model

data class DashboardMetricas(
    val totalFaturado: Double = 0.0,
    val totalVendasCount: Int = 0,
    val ticketMedio: Double = 0.0,
    val faturamentoDinheiro: Double = 0.0,
    val faturamentoPix: Double = 0.0,
    val faturamentoDebito: Double = 0.0,
    val faturamentoCredito: Double = 0.0,
    val faturamentoFiado: Double = 0.0
)
