package com.devgabriel.aulatela.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("LojaFlexSessionPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_EMPRESA_ID = "empresa_id"
        private const val KEY_NOME_EMPRESA = "nome_empresa"
        private const val KEY_NOME_OPERADOR = "nome_operador"
        private const val KEY_PERFIL = "perfil_acesso"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED = "is_logged_in"
    }

    fun salvarSessao(
        token: String? = null,
        empresaId: Long,
        nomeEmpresa: String,
        nomeOperador: String,
        perfil: String,
        userId: Long = -1L
    ) {
        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putLong(KEY_EMPRESA_ID, empresaId)
            putString(KEY_NOME_EMPRESA, nomeEmpresa)
            putString(KEY_NOME_OPERADOR, nomeOperador)
            putString(KEY_PERFIL, perfil)
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED, true)
            apply()
        }
    }

    fun getNomeEmpresa(): String = prefs.getString(KEY_NOME_EMPRESA, "Minha Empresa") ?: "Minha Empresa"

    fun getNomeOperador(): String = prefs.getString(KEY_NOME_OPERADOR, "Operador") ?: "Operador"

    fun getPerfil(): String = prefs.getString(KEY_PERFIL, "OPERADOR") ?: "OPERADOR"

    fun getEmpresaId(): Long = prefs.getLong(KEY_EMPRESA_ID, 0L)
    
    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)
    
    fun fetchAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isGerenteLoja(): Boolean = getPerfil().equals("GERENTE_LOJA", ignoreCase = true)

    fun isSuporte(): Boolean = getPerfil().equals("SUPORTE", ignoreCase = true)

    fun isLogged(): Boolean = prefs.getBoolean(KEY_IS_LOGGED, false)

    fun encerrarSessao() {
        prefs.edit().clear().apply()
    }
}
