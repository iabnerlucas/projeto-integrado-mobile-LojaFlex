package com.devgabriel.aulatela

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.model.CadastroRequest
import com.devgabriel.aulatela.databinding.ActivityCadastroBinding
import kotlinx.coroutines.launch
import java.io.IOException

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCadastro) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnFinalizarCadastro.setOnClickListener {
            cadastrarOperador()
        }

        binding.btnIrParaLogin.setOnClickListener {
            finish()
        }
    }

    private fun cadastrarOperador() {
        limparErrosAnteriores()

        val nome = binding.editNome.text.toString().trim()
        val email = binding.editEmailCadastro.text.toString().trim()
        val senha = binding.editPasswordCadastro.text.toString().trim()
        val confirmSenha = binding.editConfirmPassword.text.toString().trim()

        val perfil = if (binding.rbGerente.isChecked) "GERENTE_LOJA" else "OPERADOR"


        if (!validarCampos(nome, email, senha, confirmSenha)) {
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.cadastrarOperador(
                    CadastroRequest(nome, email, perfil, senha)
                )
                
                if (response.isSuccessful) {
                    Toast.makeText(this@CadastroActivity, "Operador cadastrado com sucesso! Faça login.", Toast.LENGTH_LONG).show()
                    finish() // Volta para a tela de login
                } else {
                    val msgErro = when (response.code()) {
                        400 -> "Dados de cadastro inválidos."
                        409 -> "Este e-mail já está cadastrado."
                        else -> "Erro no servidor (${response.code()}). Tente novamente."
                    }
                    Toast.makeText(this@CadastroActivity, msgErro, Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                Toast.makeText(this@CadastroActivity, "Sem conexão com o servidor. Verifique sua rede.", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@CadastroActivity, "Ocorreu um erro inesperado. Tente novamente.", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun validarCampos(nome: String, email: String, senha: String, confirmSenha: String): Boolean {
        var valido = true
        if (nome.isEmpty()) {
            binding.editNome.error = "Preencha o nome"
            valido = false
        }
        if (email.isEmpty()) {
            binding.editEmailCadastro.error = "Preencha o e-mail"
            valido = false
        }
        if (senha.isEmpty()) {
            binding.editPasswordCadastro.error = "Preencha a senha"
            valido = false
        }
        if (senha != confirmSenha) {
            binding.editConfirmPassword.error = "Senhas não conferem"
            valido = false
        }
        return valido
    }

    private fun limparErrosAnteriores() {
        binding.editNome.error = null
        binding.editEmailCadastro.error = null
        binding.editPasswordCadastro.error = null
        binding.editConfirmPassword.error = null
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnFinalizarCadastro.isEnabled = !isLoading
        binding.editNome.isEnabled = !isLoading
        binding.editEmailCadastro.isEnabled = !isLoading
        binding.editPasswordCadastro.isEnabled = !isLoading
        binding.editConfirmPassword.isEnabled = !isLoading
    }
}
