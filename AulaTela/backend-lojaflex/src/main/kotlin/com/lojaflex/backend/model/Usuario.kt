package com.lojaflex.backend.model

import jakarta.persistence.*

enum class PerfilAcesso {
    SUPER_ADMIN,
    GERENTE,
    CAIXA
}

@Entity
@Table(name = "tb_usuario")
data class Usuario(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val nome: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val senha: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val perfil: PerfilAcesso = PerfilAcesso.CAIXA,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "empresa_id")
    val empresa: Empresa? = null
)
