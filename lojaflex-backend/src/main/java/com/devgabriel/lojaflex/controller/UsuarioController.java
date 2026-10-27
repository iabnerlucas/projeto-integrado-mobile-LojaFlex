package com.devgabriel.lojaflex.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.devgabriel.lojaflex.model.Usuario;
import com.devgabriel.lojaflex.service.LojaService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final LojaService lojaService;

    public UsuarioController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    // 1. Listar utilizadores pendentes de aprovação
    // Consumido por: GET /api/usuarios/pendentes?empresaId=1
    @GetMapping("/pendentes")
    public ResponseEntity<List<Usuario>> listarPendentes(@RequestParam(required = false) Long empresaId) {
        List<Usuario> pendentes = lojaService.listarUsuariosPendentes(empresaId);
        return ResponseEntity.ok(pendentes);
    }

    // 2. Endpoint do Admin para aprovar o utilizador
    // Consumido por: PATCH /api/usuarios/{id}/aprovar
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Usuario> aprovar(@PathVariable Long id) {
        Usuario usuarioAprovado = lojaService.aprovarUsuario(id);
        return ResponseEntity.ok(usuarioAprovado);
    }

    // 3. Endpoint do Admin para rejeitar o utilizador
    // Consumido por: PATCH /api/usuarios/{id}/rejeitar
    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<Usuario> rejeitar(@PathVariable Long id) {
        Usuario usuarioRejeitado = lojaService.rejeitarUsuario(id);
        return ResponseEntity.ok(usuarioRejeitado);
    }
}