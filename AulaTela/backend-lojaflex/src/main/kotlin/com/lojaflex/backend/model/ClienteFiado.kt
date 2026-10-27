package com.lojaflex.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "tb_cliente_fiado")
data class ClienteFiado(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var nomeCliente: String = "",

    var cpfCliente: String = "",
    var telefoneWhatsapp: String = "",
    var fotoClienteUri: String? = null,

    var limiteCredito: Double = 200.0,
    var valorDevido: Double = 0.0,
    var itensComprados: String = "",
    var dataCompra: String = "",
    var horaCompra: String = "",
    var dataVencimento: String = "",
    var pago: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    var empresa: Empresa? = null
)
