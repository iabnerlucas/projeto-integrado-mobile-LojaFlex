package com.lojaflex.backend.controller

import com.lojaflex.backend.dto.*
import com.lojaflex.backend.model.PerfilAcesso
import com.lojaflex.backend.model.Usuario
import com.lojaflex.backend.repository.CadastroRepository
import com.lojaflex.backend.repository.EmpresaRepository
import com.lojaflex.backend.repository.LoginRepository
import com.lojaflex.backend.repository.UsuarioRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val usuarioRepository: UsuarioRepository,
    private val empresaRepository: EmpresaRepository,
    private val loginRepository: LoginRepository,
    private val cadastroRepository: CadastroRepository
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        // 1. Tenta autenticar na tabela de usuários de operação
        val usuario = usuarioRepository.findByEmail(request.email)
        if (usuario != null && usuario.senha == request.senha) {
            val usuarioDTO = UsuarioResponseDTO(
                id = usuario.id!!,
                nome = usuario.nome,
                email = usuario.email,
                perfil = usuario.perfil,
                empresaId = usuario.empresa?.id,
                nomeEmpresa = usuario.empresa?.nomeFantasia ?: "LojaFlex Suporte SaaS"
            )
            val token = "jwt_token_${usuario.id}_${System.currentTimeMillis()}"
            return ResponseEntity.ok(LoginResponse(token, usuarioDTO))
        }

        // 2. Tenta autenticar na tabela login (cadastro da empresa)
        val loginAdmin = loginRepository.findByEmailAdmin(request.email)
        if (loginAdmin != null && loginAdmin.senhaHash == request.senha) {
            val cadastro = loginAdmin.cadastro
            val usuarioDTO = UsuarioResponseDTO(
                id = loginAdmin.id ?: 1L,
                nome = cadastro?.nomeDono ?: "Administrador Loja",
                email = loginAdmin.emailAdmin,
                perfil = PerfilAcesso.GERENTE,
                empresaId = cadastro?.id ?: 1L,
                nomeEmpresa = cadastro?.nomeLoja ?: "LojaFlex Loja"
            )
            val token = "jwt_token_admin_${loginAdmin.id}_${System.currentTimeMillis()}"
            return ResponseEntity.ok(LoginResponse(token, usuarioDTO))
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso Negado: E-mail ou senha incorretos no banco de dados")
    }

    @PostMapping("/cadastrar")
    fun cadastrar(@RequestBody request: CadastroRequest): ResponseEntity<Any> {
        val empresa = if (request.empresaId != null) empresaRepository.findById(request.empresaId).orElse(null) else null

        val novoUsuario = Usuario(
            nome = request.nome,
            email = request.email,
            senha = request.senha,
            perfil = try { PerfilAcesso.valueOf(request.perfil ?: "CAIXA") } catch (_: Exception) { PerfilAcesso.CAIXA },
            empresa = empresa
        )

        val salvo = usuarioRepository.save(novoUsuario)
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }
}
