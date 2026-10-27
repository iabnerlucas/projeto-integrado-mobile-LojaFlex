package com.devgabriel.lojaflex.controller;

import com.devgabriel.lojaflex.model.Produto;
import com.devgabriel.lojaflex.service.LojaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final LojaService lojaService;

    public ProdutoController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(lojaService.listarProdutos(empresaId, busca));
    }

    @PostMapping
    public ResponseEntity<Produto> salvarProduto(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestBody Produto produto) {
        return ResponseEntity.ok(lojaService.salvarProduto(empresaId, produto));
    }

    @PutMapping("/{id}/estoque")
    public ResponseEntity<Produto> ajustarEstoque(@PathVariable Long id, @RequestParam Integer quantidade) {
        return ResponseEntity.ok(lojaService.ajustarEstoque(id, quantidade));
    }
}