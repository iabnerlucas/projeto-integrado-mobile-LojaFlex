package com.devgabriel.lojaflex.dto;

import java.math.BigDecimal;

public class PedidoResponse {
	private Long pedidoId;
	private String status;
	private BigDecimal valorTotal;

	public PedidoResponse(Long pedidoId, String status, BigDecimal valorTotal) {
		this.pedidoId = pedidoId;
		this.status = status;
		this.valorTotal = valorTotal;
	}

	public Long getPedidoId() {
		return pedidoId;
	}

	public void setPedidoId(Long pedidoId) {
		this.pedidoId = pedidoId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
}