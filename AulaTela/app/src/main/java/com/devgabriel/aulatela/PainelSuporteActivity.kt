package com.devgabriel.aulatela

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devgabriel.aulatela.adapter.LojaPendenteAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.model.AprovarLojaRequest
import com.devgabriel.aulatela.data.model.CadastroEmpresaRequest
import com.devgabriel.aulatela.data.model.Empresa
import com.devgabriel.aulatela.databinding.ActivityPainelSuporteBinding
import kotlinx.coroutines.launch
import java.net.URLEncoder

class PainelSuporteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPainelSuporteBinding
    private val listaLojasPendentes = mutableListOf<Empresa>()
    private lateinit var adapter: LojaPendenteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPainelSuporteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainPainelSuporte) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        carregarSolicitacoesSuporte()

        binding.btnVoltarSuporte.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = LojaPendenteAdapter(
            listaLojasPendentes,
            onAprovarClicked = { loja -> abrirDialogAprovacaoCredenciais(loja) },
            onEnviarWhatsappClicked = { loja -> enviarAcessoWhatsapp(loja, loja.emailAdmin ?: "gerente@loja.com", "LojaFlex#2026") }
        )
        binding.rvLojasPendentes.layoutManager = LinearLayoutManager(this)
        binding.rvLojasPendentes.adapter = adapter
    }

    private fun carregarSolicitacoesSuporte() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarEmpresasPendentes()
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    listaLojasPendentes.clear()
                    listaLojasPendentes.addAll(response.body()!!)
                }
            } catch (_: Exception) {}
            atualizarResumoMétricas()
        }
    }

    private fun atualizarResumoMétricas() {
        adapter.atualizarLista(listaLojasPendentes)
        binding.tvTotalLojasPendentes.text = "${listaLojasPendentes.size} solicitações"
        binding.tvTotalLojasAtivas.text = "Empresas cadastradas"
    }

    private fun abrirDialogAprovacaoCredenciais(loja: Empresa) {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val emailGerentePadrao = loja.emailAdmin ?: "gerente@loja.com"
        val etEmail = EditText(this).apply {
            setText(emailGerentePadrao)
            hint = "E-mail de Acesso do Gerente"
        }
        val etSenha = EditText(this).apply {
            setText("LojaFlex#2026")
            hint = "Senha do Gerente"
        }

        layout.addView(etEmail)
        layout.addView(etSenha)

        AlertDialog.Builder(this)
            .setTitle("Aprovar Loja: ${loja.nomeFantasia}")
            .setMessage("Defina as credenciais de acesso do Gerente da empresa:")
            .setView(layout)
            .setPositiveButton("Aprovar & Enviar WhatsApp") { _, _ ->
                val emailFinal = etEmail.text.toString().trim()
                val senhaFinal = etSenha.text.toString().trim()

                lifecycleScope.launch {
                    try {
                        val requestAprovacao = AprovarLojaRequest(emailFinal, senhaFinal)
                        RetrofitClient.apiService.aprovarEmpresa(loja.id ?: 1L, requestAprovacao)
                    } catch (_: Exception) {}
                    Toast.makeText(this@PainelSuporteActivity, "Empresa '${loja.nomeFantasia}' aprovada no sistema!", Toast.LENGTH_SHORT).show()
                    enviarAcessoWhatsapp(loja, emailFinal, senhaFinal)
                    listaLojasPendentes.remove(loja)
                    atualizarResumoMétricas()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarAcessoWhatsapp(loja: Empresa, email: String, senha: String) {
        val mensagemAcesso = """
            Olá! 👋
            
            Sua loja *${loja.nomeFantasia}* foi APROVADA e ativada com sucesso no aplicativo *LojaFlex*! 🎉
            
            🔑 *SUAS CREDENCIAIS DE ACESSO (PERFIL GERENTE):*
            • *E-mail:* `$email`
            • *Senha:* `$senha`
            
            📲 *COMO ACESSAR O APP:*
            1. Abra o aplicativo LojaFlex no celular.
            2. Digite seu e-mail e senha acima na tela de Login.
            3. Na tela inicial, você já poderá criar a conta dos seus Caixas/Funcionários no menu *'Criar Conta Operador'*.
            
            Caso precise de suporte, estamos à disposição por este número.
            Bons negócios! 🚀
        """.trimIndent()

        val foneLimpo = (loja.telefone ?: "").replace("[^0-9]".toRegex(), "")
        val foneFinal = if (foneLimpo.startsWith("55")) foneLimpo else "55$foneLimpo"

        try {
            val uri = Uri.parse("whatsapp://send?phone=$foneFinal&text=${URLEncoder.encode(mensagemAcesso, "UTF-8")}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Acesso aprovado! Envie as credenciais para o responsável.", Toast.LENGTH_LONG).show()
        }
    }
}
