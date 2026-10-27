package com.lojaflex.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tb_empresa")
data class Empresa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val nomeFantasia: String = "",

    val razaoSocial: String? = null,

    @Column(nullable = false, unique = true)
    val cnpjCpf: String = "",

    val telefone: String? = null,
    val emailAdmin: String? = null,
    val nomeAdmin: String? = null,
    val senhaAdmin: String? = null,

    var ativa: Boolean = true,
    val dataCadastro: LocalDateTime = LocalDateTime.now()
)
