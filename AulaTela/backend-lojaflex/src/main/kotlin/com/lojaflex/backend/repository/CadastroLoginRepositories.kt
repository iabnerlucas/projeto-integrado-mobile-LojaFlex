package com.lojaflex.backend.repository

import com.lojaflex.backend.model.CadastroEntity
import com.lojaflex.backend.model.LoginEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CadastroRepository : JpaRepository<CadastroEntity, Long> {
    fun findByCelularWhatsapp(celularWhatsapp: String): CadastroEntity?
}

@Repository
interface LoginRepository : JpaRepository<LoginEntity, Long> {
    fun findByEmailAdmin(emailAdmin: String): LoginEntity?
}
