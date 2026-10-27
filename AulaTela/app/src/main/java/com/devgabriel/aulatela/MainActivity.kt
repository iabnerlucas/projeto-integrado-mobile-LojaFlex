
package com.devgabriel.aulatela

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.LoginRequest
import com.devgabriel.aulatela.data.model.PerfilEnum
import com.devgabriel.aulatela.databinding.ActivityMenuBinding
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitClient.inicializar(this)
        sessionManager = SessionManager(this)

        // Se já existir um token salvo, direciona para a tela correspondente.
        val tokenSalvo = sessionManager.fetchAuthToken()

        if (!tokenSalvo.isNullOrBlank()) {
            val intentDestination =
                if (sessionManager.isSuporte()) {
                    Intent(this, HomeActivity::class.java)
                } else {
                    Intent(this, SelecaoPerfilActivity::class.java)
                }

            intentDestination.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intentDestination)
            finish()
            return
        }

        // Ajusta a tela para as barras do sistema Android.
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Botão de login.
        binding.btnEntrar.setOnClickListener {
            fazerLogin()
        }

        // Botão para cadastrar uma empresa.
        binding.btnCadastrarEmpresa.setOnClickListener {
            val intent = Intent(
                this,
                CadastroEmpresaActivity::class.java
            )

            startActivity(intent)
        }

        // Botão de suporte pelo WhatsApp.
        binding.btnSuporte.setOnClickListener {
            val mensagemSuporte =
                "Olá, Suporte LojaFlex! Preciso de atendimento sobre o aplicativo."

            val numeroSuporte = "5561996076814"

            try {
                val uri = Uri.parse(
                    "https://api.whatsapp.com/send" +
                            "?phone=$numeroSuporte" +
                            "&text=${Uri.encode(mensagemSuporte)}"
                )

                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)

            } catch (e: Exception) {
                exibirMensagem(
                    "Suporte LojaFlex: suporte@lojaflex.com.br"
                )
            }
        }
    }

    private fun fazerLogin() {
        limparErrosAnteriores()

        val emailOuId =
            binding.editEmail.text.toString().trim()

        val senhaLoja =
            binding.editPassword.text.toString().trim()

        if (!validarCampos(emailOuId, senhaLoja)) {
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val request = LoginRequest(
                    login = emailOuId,
                    senha = senhaLoja
                )

                val response =
                    RetrofitClient.apiService.login(request)

                if (!response.isSuccessful) {

                    val erroCorpo = try {
                        response.errorBody()?.string().orEmpty()
                    } catch (e: Exception) {
                        ""
                    }

                    when (response.code()) {

                        401 -> {
                            if (
                                erroCorpo.contains(
                                    "aguardando aprovação",
                                    ignoreCase = true
                                )
                            ) {
                                exibirDialogoPendente()
                            } else {
                                exibirMensagem(
                                    "Credenciais inválidas. " +
                                            "Verifique seu login e senha."
                                )
                            }
                        }

                        403 -> {
                            if (
                                erroCorpo.contains(
                                    "aguardando aprovação",
                                    ignoreCase = true
                                )
                            ) {
                                exibirDialogoPendente()
                            } else {
                                exibirMensagem(
                                    "Acesso negado. " +
                                            "Verifique se a loja ou o usuário " +
                                            "está aprovado."
                                )
                            }
                        }

                        404 -> {
                            exibirMensagem(
                                "Usuário ou loja não encontrados no sistema."
                            )
                        }

                        else -> {
                            exibirMensagem(
                                "Erro no servidor " +
                                        "(Código ${response.code()})."
                            )
                        }
                    }

                    return@launch
                }

                // =====================================================
                // CORREÇÃO PRINCIPAL:
                // O back-end Java retorna:
                //
                // {
                //   "token": "...",
                //   "usuario": {
                //     "id": 1,
                //     "nome": "...",
                //     "perfil": "...",
                //     "empresa": {
                //       "id": 1,
                //       "nomeFantasia": "..."
                //     }
                //   }
                // }
                //
                // Por isso, os dados são lidos de loginBody.usuario.
                // =====================================================

                val loginBody = response.body()

                if (loginBody == null) {
                    exibirMensagem(
                        "O servidor retornou uma resposta vazia."
                    )
                    return@launch
                }

                val usuario = loginBody.usuario

                if (usuario == null) {
                    exibirMensagem(
                        "Falha no login: o servidor não retornou " +
                                "os dados do usuário."
                    )
                    return@launch
                }

                val userId = usuario.id

                if (userId == null || userId <= 0L) {
                    exibirMensagem(
                        "Falha no login: o ID do usuário é inválido."
                    )
                    return@launch
                }

                val tokenJwt = loginBody.token

                if (tokenJwt.isNullOrBlank()) {
                    exibirMensagem(
                        "Falha na autenticação: o servidor não retornou " +
                                "um token válido."
                    )
                    return@launch
                }

                val perfilUsuarioStr =
                    usuario.perfil
                        ?.trim()
                        ?.uppercase()
                        ?.takeIf { it.isNotEmpty() }
                        ?: PerfilEnum.OPERADOR.name

                val nomeOperador =
                    usuario.nome
                        ?.takeIf { it.isNotBlank() }
                        ?: usuario.login
                            ?.takeIf { it.isNotBlank() }
                        ?: "Operador"

                val empresaId =
                    usuario.empresa?.id ?: 0L

                val nomeEmpresa =
                    usuario.empresa?.nomeFantasia
                        ?.takeIf { it.isNotBlank() }
                        ?: "Minha Empresa"

                // Salva somente os dados efetivamente retornados
                // pelo servidor. Não cria token fictício.
                sessionManager.encerrarSessao()

                sessionManager.salvarSessao(
                    token = tokenJwt,
                    empresaId = empresaId,
                    nomeEmpresa = nomeEmpresa,
                    nomeOperador = nomeOperador,
                    perfil = perfilUsuarioStr,
                    userId = userId
                )

                exibirMensagem(
                    "Bem-vindo, $nomeOperador!"
                )

                // Mantém a navegação original do aplicativo.
                val intentDestination =
                    if (
                        perfilUsuarioStr.equals(
                            PerfilEnum.SUPORTE.name,
                            ignoreCase = true
                        )
                    ) {
                        Intent(
                            this@MainActivity,
                            HomeActivity::class.java
                        )
                    } else {
                        Intent(
                            this@MainActivity,
                            SelecaoPerfilActivity::class.java
                        )
                    }

                intentDestination.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intentDestination)
                finish()

            } catch (e: IOException) {

                exibirMensagem(
                    "Falha na conexão. Verifique sua rede, " +
                            "o endereço da API e se o back-end está rodando."
                )

            } catch (e: Exception) {

                e.printStackTrace()

                exibirMensagem(
                    "Erro ao processar o login: " +
                            (e.message ?: "erro desconhecido")
                )

            } finally {
                setLoading(false)
            }
        }
    }

    private fun validarCampos(
        email: String,
        senha: String
    ): Boolean {

        var valido = true

        if (email.isEmpty()) {
            binding.editEmail.error =
                "Informe o ID da loja ou e-mail"

            valido = false
        }

        if (senha.isEmpty()) {
            binding.editPassword.error =
                "Informe a senha do sistema"

            valido = false
        }

        return valido
    }

    private fun limparErrosAnteriores() {
        binding.editEmail.error = null
        binding.editPassword.error = null
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBarLogin.visibility =
            if (isLoading) View.VISIBLE else View.GONE

        binding.btnEntrar.isEnabled = !isLoading
        binding.editEmail.isEnabled = !isLoading
        binding.editPassword.isEnabled = !isLoading
        binding.btnCadastrarEmpresa.isEnabled = !isLoading
        binding.btnSuporte.isEnabled = !isLoading
    }

    private fun exibirMensagem(mensagem: String) {
        Toast.makeText(
            this,
            mensagem,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun exibirDialogoPendente() {
        AlertDialog.Builder(this)
            .setTitle("Aguardando Aprovação")
            .setMessage(
                "Seu cadastro foi recebido, mas ainda está " +
                        "aguardando a aprovação do administrador da loja. " +
                        "Tente novamente mais tarde."
            )
            .setPositiveButton("OK", null)
            .show()
    }
}
