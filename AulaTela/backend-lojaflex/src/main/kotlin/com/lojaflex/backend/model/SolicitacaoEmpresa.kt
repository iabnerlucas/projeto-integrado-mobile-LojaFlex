package com.lojaflex.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_solicitacao_empresa")
data class SolicitacaoEmpresa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val cnpjCpf: String = "",

    @Column(nullable = false)
    val nomeFantasia: String = "",

    val telefone: String? = null,
    val nomeAdmin: String = "",
    val email: String = "",

    var aprovada: Boolean = false,
    val dataSolicitacao: LocalDateTime = LocalDateTime.now()
)
