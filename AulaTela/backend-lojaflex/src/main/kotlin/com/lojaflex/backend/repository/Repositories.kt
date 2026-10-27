package com.lojaflex.backend.repository

import com.lojaflex.backend.model.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmpresaRepository : JpaRepository<Empresa, Long> {
    fun findByCnpjCpf(cnpjCpf: String): Empresa?
}

@Repository
interface UsuarioRepository : JpaRepository<Usuario, Long> {
    fun findByEmail(email: String): Usuario?
}

@Repository
interface ProdutoRepository : JpaRepository<Produto, Long> {
    fun findByEmpresaId(empresaId: Long): List<Produto>
    fun findByEmpresaIdAndNomeContainingIgnoreCase(empresaId: Long, nome: String): List<Produto>
    fun findByCodigoBarra(codigoBarra: String): Produto?
}

@Repository
interface FornecedorRepository : JpaRepository<Fornecedor, Long> {
    fun findByEmpresaId(empresaId: Long): List<Fornecedor>
}

@Repository
interface ClienteFiadoRepository : JpaRepository<ClienteFiado, Long> {
    fun findByEmpresaId(empresaId: Long): List<ClienteFiado>
}

@Repository
interface ContaPagarRepository : JpaRepository<ContaPagar, Long> {
    fun findByEmpresaId(empresaId: Long): List<ContaPagar>
}

@Repository
interface PedidoRepository : JpaRepository<Pedido, Long> {
    fun findByEmpresaId(empresaId: Long): List<Pedido>
}

@Repository
interface SolicitacaoEmpresaRepository : JpaRepository<SolicitacaoEmpresa, Long> {
    fun findByAprovadaFalse(): List<SolicitacaoEmpresa>
}
