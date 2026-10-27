package com.lojaflex.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "cadastro")
data class CadastroEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "nome_loja", nullable = false, length = 150)
    var nomeLoja: String = "",

    @Column(name = "nome_dono", nullable = false, length = 150)
    var nomeDono: String = "",

    @Column(name = "celular_whatsapp", nullable = false, length = 20)
    var celularWhatsapp: String = "",

    @Column(name = "cpf_cnpj", length = 18)
    var cpfCnpj: String? = null,

    @Column(name = "email_contato", length = 150)
    var emailContato: String? = null,

    @Column(name = "criado_em", nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now(),

    @OneToOne(mappedBy = "cadastro", cascade = [CascadeType.ALL], orphanRemoval = true)
    var login: LoginEntity? = null
)
