package com.devgabriel.lojaflex.controller;

import com.devgabriel.lojaflex.dto.DashboardMetricas;
import com.devgabriel.lojaflex.model.*;
import com.devgabriel.lojaflex.service.LojaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OperacaoController {

    private final LojaService lojaService;

    public OperacaoController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping("/dashboard/metricas")
    public ResponseEntity<DashboardMetricas> obterMetricas(@RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(lojaService.obterMetricasDashboard(empresaId));
    }

    @GetMapping("/fiado")
    public ResponseEntity<List<ClienteFiado>> listarFiado(@RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(lojaService.listarClientesFiado(empresaId));
    }

    @PostMapping("/fiado")
    public ResponseEntity<ClienteFiado> cadastrarFiado(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestBody ClienteFiado cliente) {
        return ResponseEntity.ok(lojaService.salvarClienteFiado(empresaId, cliente));
    }

    @GetMapping("/financeiro/contas")
    public ResponseEntity<List<ContaPagar>> listarContasPagar(@RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(lojaService.listarContasPagar(empresaId));
    }

    @PostMapping("/financeiro/contas")
    public ResponseEntity<ContaPagar> cadastrarContaPagar(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestBody ContaPagar conta) {
        return ResponseEntity.ok(lojaService.salvarContaPagar(empresaId, conta));
    }

    @PutMapping("/financeiro/contas/{id}/baixar")
    public ResponseEntity<ContaPagar> baixarConta(@PathVariable Long id) {
        return ResponseEntity.ok(lojaService.darBaixaConta(id));
    }

    @GetMapping("/fornecedores")
    public ResponseEntity<List<Fornecedor>> listarFornecedores(@RequestHeader("X-Empresa-Id") Long empresaId) {
        return ResponseEntity.ok(lojaService.listarFornecedores(empresaId));
    }

    @PostMapping("/fornecedores")
    public ResponseEntity<Fornecedor> cadastrarFornecedor(
            @RequestHeader("X-Empresa-Id") Long empresaId,
            @RequestBody Fornecedor fornecedor) {
        return ResponseEntity.ok(lojaService.salvarFornecedor(empresaId, fornecedor));
    }

    @GetMapping("/suporte/lojas-pendentes")
    public ResponseEntity<List<Empresa>> listarLojasPendentes() {
        return ResponseEntity.ok(lojaService.listarLojasPendentes());
    }

    @PutMapping("/suporte/lojas/{id}/aprovar")
    public ResponseEntity<Empresa> aprovarLoja(@PathVariable Long id) {
        return ResponseEntity.ok(lojaService.aprovarLoja(id));
    }
}