package com.lojaflex.backend.controller

import com.lojaflex.backend.dto.CadastroEmpresaRequest
import com.lojaflex.backend.model.CadastroEntity
import com.lojaflex.backend.model.LoginEntity
import com.lojaflex.backend.model.SolicitacaoEmpresa
import com.lojaflex.backend.repository.CadastroRepository
import com.lojaflex.backend.repository.EmpresaRepository
import com.lojaflex.backend.repository.LoginRepository
import com.lojaflex.backend.repository.SolicitacaoEmpresaRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/empresas")
class EmpresaController(
    private val empresaRepository: EmpresaRepository,
    private val solicitacaoRepository: SolicitacaoEmpresaRepository,
    private val cadastroRepository: CadastroRepository,
    private val loginRepository: LoginRepository
) {

    @PostMapping
    fun cadastrarEmpresa(@RequestBody request: CadastroEmpresaRequest): ResponseEntity<CadastroEntity> {
        val novoCadastro = CadastroEntity(
            nomeLoja = request.nomeFantasia,
            nomeDono = request.nomeAdmin,
            celularWhatsapp = request.telefone ?: "",
            cpfCnpj = request.cnpjCpf,
            emailContato = request.emailAdmin
        )

        val cadastroSalvo = cadastroRepository.save(novoCadastro)

        val novoLogin = LoginEntity(
            cadastro = cadastroSalvo,
            emailAdmin = request.emailAdmin,
            senhaHash = request.senhaAdmin ?: "LojaFlex#2026"
        )
        loginRepository.save(novoLogin)

        val solicitacao = SolicitacaoEmpresa(
            cnpjCpf = request.cnpjCpf,
            nomeFantasia = request.nomeFantasia,
            telefone = request.telefone,
            nomeAdmin = request.nomeAdmin,
            email = request.emailAdmin
        )
        solicitacaoRepository.save(solicitacao)

        return ResponseEntity.status(HttpStatus.CREATED).body(cadastroSalvo)
    }

    @GetMapping
    fun listarEmpresas(): ResponseEntity<List<CadastroEntity>> {
        return ResponseEntity.ok(cadastroRepository.findAll())
    }
}
