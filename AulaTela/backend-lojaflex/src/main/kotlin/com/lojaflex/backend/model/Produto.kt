package com.lojaflex.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "tb_produto")
data class Produto(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var nome: String = "",

    @Column(nullable = false)
    var preco: Double = 0.0,

    var precoCusto: Double = 0.0,

    var codigoBarra: String? = null,

    @Column(nullable = false)
    var quantidadeEstoque: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    var empresa: Empresa? = null
)
