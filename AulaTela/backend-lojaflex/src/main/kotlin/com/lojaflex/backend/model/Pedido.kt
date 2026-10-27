package com.lojaflex.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_pedido")
data class Pedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val formaPagamento: String = "",

    @Column(nullable = false)
    val valorTotal: Double = 0.0,

    @Column(nullable = false)
    val valorRecebido: Double = 0.0,

    val troco: Double = 0.0,

    val dataHora: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    val empresa: Empresa? = null,

    @OneToMany(mappedBy = "pedido", cascade = [CascadeType.ALL], orphanRemoval = true)
    val itens: MutableList<ItemPedido> = mutableListOf()
)

@Entity
@Table(name = "tb_item_pedido")
data class ItemPedido(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    var pedido: Pedido? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    val produto: Produto? = null,

    @Column(nullable = false)
    val quantidade: Int = 1,

    @Column(nullable = false)
    val precoUnitario: Double = 0.0
)
