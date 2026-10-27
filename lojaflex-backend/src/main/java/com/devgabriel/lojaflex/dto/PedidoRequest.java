package com.devgabriel.lojaflex.dto;

import java.util.List;

public class PedidoRequest {

    private Long usuarioId;
    private List<ItemPedidoRequest> itens;
    private String formaPagamento;

    public PedidoRequest() {
    }

    public PedidoRequest(Long usuarioId, List<ItemPedidoRequest> itens, String formaPagamento) {
        this.usuarioId = usuarioId;
        this.itens = itens;
        this.formaPagamento = formaPagamento;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}