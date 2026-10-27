package com.devgabriel.lojaflex.dto;

import java.math.BigDecimal;

public class DashboardMetricas {
	private BigDecimal totalVendasHoje;
	private Integer quantidadeVendasHoje;
	private Integer produtosEstoqueBaixo;
	private BigDecimal totalContasPagar;

	public DashboardMetricas(BigDecimal totalVendasHoje, Integer quantidadeVendasHoje, Integer produtosEstoqueBaixo,
			BigDecimal totalContasPagar) {
		this.totalVendasHoje = totalVendasHoje;
		this.quantidadeVendasHoje = quantidadeVendasHoje;
		this.produtosEstoqueBaixo = produtosEstoqueBaixo;
		this.totalContasPagar = totalContasPagar;
	}

	public BigDecimal getTotalVendasHoje() {
		return totalVendasHoje;
	}

	public void setTotalVendasHoje(BigDecimal totalVendasHoje) {
		this.totalVendasHoje = totalVendasHoje;
	}

	public Integer getQuantidadeVendasHoje() {
		return quantidadeVendasHoje;
	}

	public void setQuantidadeVendasHoje(Integer quantidadeVendasHoje) {
		this.quantidadeVendasHoje = quantidadeVendasHoje;
	}

	public Integer getProdutosEstoqueBaixo() {
		return produtosEstoqueBaixo;
	}

	public void setProdutosEstoqueBaixo(Integer produtosEstoqueBaixo) {
		this.produtosEstoqueBaixo = produtosEstoqueBaixo;
	}

	public BigDecimal getTotalContasPagar() {
		return totalContasPagar;
	}

	public void setTotalContasPagar(BigDecimal totalContasPagar) {
		this.totalContasPagar = totalContasPagar;
	}
}