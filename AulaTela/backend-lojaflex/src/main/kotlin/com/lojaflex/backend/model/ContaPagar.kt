package com.lojaflex.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "tb_conta_pagar")
data class ContaPagar(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var descricao: String = "",

    @Column(nullable = false)
    var valor: Double = 0.0,

    @Column(nullable = false)
    var dataVencimento: String = "",

    var paga: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    var empresa: Empresa? = null
)
