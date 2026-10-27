package com.lojaflex.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "tb_fornecedor")
data class Fornecedor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var nomeFantasia: String = "",

    var razaoSocial: String? = null,
    var cnpj: String? = null,
    var telefone: String? = null,
    var email: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    var empresa: Empresa? = null
)
