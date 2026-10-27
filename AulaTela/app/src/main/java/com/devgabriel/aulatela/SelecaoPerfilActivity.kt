package com.devgabriel.aulatela

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.databinding.ActivitySelecaoPerfilBinding

class SelecaoPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySelecaoPerfilBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySelecaoPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainSelecaoPerfil) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvNomeLojaHeader.text = sessionManager.getNomeEmpresa()

        binding.btnSairSelecao.setOnClickListener {
            sessionManager.encerrarSessao()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // ABRIR CAIXA & ENTRAR NO PDV (CAIXA / OPERADOR - SEM SENHA)
        binding.btnEntrarFrenteCaixa.setOnClickListener {
            val nomeOperador = binding.editNomeOperadorCaixa.text.toString().trim()
            if (nomeOperador.isEmpty()) {
                binding.editNomeOperadorCaixa.error = "Informe o nome do operador do caixa"
                return@setOnClickListener
            }

            sessionManager.salvarSessao(
                token = "caixa_jwt_token",
                empresaId = sessionManager.getEmpresaId(),
                nomeEmpresa = sessionManager.getNomeEmpresa(),
                nomeOperador = nomeOperador,
                perfil = "OPERADOR"
            )

            Toast.makeText(this, "Caixa Aberto! Operador: $nomeOperador", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PdvActivity::class.java)
            startActivity(intent)
        }

        // ENTRAR NO PAINEL GERENCIAL (GERENTE - EXIGE SENHA)
        binding.btnEntrarPainelGerente.setOnClickListener {
            val senhaGerente = binding.editSenhaAcessoGerente.text.toString().trim()
            if (senhaGerente.isEmpty()) {
                binding.editSenhaAcessoGerente.error = "Informe a senha do gerente"
                return@setOnClickListener
            }

            sessionManager.salvarSessao(
                token = "gerente_jwt_token",
                empresaId = sessionManager.getEmpresaId(),
                nomeEmpresa = sessionManager.getNomeEmpresa(),
                nomeOperador = sessionManager.getNomeOperador().ifEmpty { "Gerente da Loja" },
                perfil = "GERENTE_LOJA"
            )

            Toast.makeText(this, "Acesso Gerencial Concedido!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}
