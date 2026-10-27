package com.lojaflex.backend.controller

import com.lojaflex.backend.dto.AprovarLojaRequest
import com.lojaflex.backend.dto.CadastroEmpresaRequest
import com.lojaflex.backend.repository.LoginRepository
import com.lojaflex.backend.repository.SolicitacaoEmpresaRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/suporte")
class SuporteController(
    private val solicitacaoRepository: SolicitacaoEmpresaRepository,
    private val loginRepository: LoginRepository
) {

    @GetMapping("/solicitacoes")
    fun getSolicitacoesSuporte(): ResponseEntity<List<CadastroEmpresaRequest>> {
        val pendentes = solicitacaoRepository.findByAprovadaFalse().map {
            CadastroEmpresaRequest(
                id = it.id,
                cnpjCpf = it.cnpjCpf,
                nomeFantasia = it.nomeFantasia,
                telefone = it.telefone,
                nomeAdmin = it.nomeAdmin,
                emailAdmin = it.email,
                senhaAdmin = "LojaFlex#2026"
            )
        }
        return ResponseEntity.ok(pendentes)
    }

    @PutMapping("/solicitacoes/{id}/aprovar")
    fun aprovarSolicitacaoSuporte(@PathVariable id: Long, @RequestBody request: AprovarLojaRequest): ResponseEntity<Void> {
        val solicitacao = solicitacaoRepository.findById(id).orElse(null)
        if (solicitacao != null) {
            val loginEntity = loginRepository.findByEmailAdmin(solicitacao.email)
            if (loginEntity != null) {
                loginEntity.emailAdmin = request.emailGerente
                loginEntity.senhaHash = request.senhaGerente
                loginRepository.save(loginEntity)
            }
            
            solicitacao.aprovada = true
            solicitacaoRepository.save(solicitacao)
        }
        return ResponseEntity.ok().build()
    }
}
