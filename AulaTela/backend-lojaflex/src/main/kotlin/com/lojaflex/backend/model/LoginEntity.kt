package com.lojaflex.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "login")
data class LoginEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cadastro_id", nullable = false)
    var cadastro: CadastroEntity? = null,

    @Column(name = "email_admin", nullable = false, unique = true, length = 150)
    var emailAdmin: String = "",

    @Column(name = "senha_hash", nullable = false, length = 255)
    var senhaHash: String = ""
)
