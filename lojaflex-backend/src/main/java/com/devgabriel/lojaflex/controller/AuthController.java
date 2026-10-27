package com.devgabriel.lojaflex.controller;

import com.devgabriel.lojaflex.dto.*;
import com.devgabriel.lojaflex.model.Empresa;
import com.devgabriel.lojaflex.model.Usuario;
import com.devgabriel.lojaflex.service.LojaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final LojaService lojaService;

    public AuthController(LojaService lojaService) {
        this.lojaService = lojaService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = lojaService.autenticar(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Usuario> cadastrar(
            @RequestHeader(value = "X-Empresa-Id", required = false) Long headerEmpresaId,
            @RequestParam(required = false) Long empresaId,
            @RequestBody CadastroRequest request) {
        
        Long idFinal = (empresaId != null) ? empresaId : headerEmpresaId;
        return ResponseEntity.status(HttpStatus.CREATED).body(lojaService.cadastrarUsuario(request, idFinal));
    }

    @PostMapping("/empresas/solicitar-cadastro")
    public ResponseEntity<Empresa> solicitarCadastroEmpresa(@RequestBody CadastroEmpresaRequest request) {
        return ResponseEntity.ok(lojaService.solicitarCadastroEmpresa(request));
    }

    @PostMapping("/empresas/criar-completa")
    public ResponseEntity<Empresa> criarEmpresaCompleta(@RequestBody CadastroEmpresaRequest request) {
        return ResponseEntity.ok(lojaService.criarEmpresaCompleta(request));
    }
}