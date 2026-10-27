package com.devgabriel.lojaflex.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devgabriel.lojaflex.dto.PedidoRequest;
import com.devgabriel.lojaflex.dto.PedidoResponse;
import com.devgabriel.lojaflex.service.LojaService;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final LojaService lojaService;

    public PedidoController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> realizarPedido(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestBody PedidoRequest request) {
        return ResponseEntity.ok(lojaService.processarPedido(empresaId, request));
    }
}