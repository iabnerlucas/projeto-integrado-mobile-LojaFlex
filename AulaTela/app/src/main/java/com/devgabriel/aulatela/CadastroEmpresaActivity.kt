package com.devgabriel.aulatela

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.CadastroEmpresaRequest
import com.devgabriel.aulatela.databinding.ActivityCadastroEmpresaBinding
import kotlinx.coroutines.launch

class CadastroEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroEmpresaBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainCadastroEmpresa) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnFinalizarEmpresa.setOnClickListener {
            solicitarCadastroViaWhatsapp()
        }

        binding.btnVoltarLoginEmpresa.setOnClickListener {
            finish()
        }
    }

    private fun solicitarCadastroViaWhatsapp() {
        val nomeLoja = binding.editNomeLoja.text.toString().trim()
        val nomeAdmin = binding.editNomeAdmin.text.toString().trim()
        val tel = binding.editTelefoneEmpresa.text.toString().trim()
        val cnpj = binding.editCnpj.text.toString().trim()
        val email = binding.editEmailEmpresa.text.toString().trim()

        if (nomeLoja.isEmpty()) {
            binding.editNomeLoja.error = "Informe como a loja vai ser chamada"
            return
        }
        if (nomeAdmin.isEmpty()) {
            binding.editNomeAdmin.error = "Informe o nome do dono/responsável"
            return
        }
        if (tel.isEmpty()) {
            binding.editTelefoneEmpresa.error = "Informe o celular/WhatsApp"
            return
        }

        val request = CadastroEmpresaRequest(
            cnpjCpf = cnpj.ifEmpty { "Não informado" },
            nomeFantasia = nomeLoja,
            telefone = tel,
            nomeAdmin = nomeAdmin,
            emailAdmin = email.ifEmpty { "contato@${nomeLoja.lowercase().replace(" ", "")}.com" },
            senhaAdmin = null
        )

        lifecycleScope.launch {
            try {
                RetrofitClient.apiService.solicitarCadastroEmpresa(request)
            } catch (_: Exception) {}
        }

        val mensagemComercial = """
            Olá, Suporte Comercial LojaFlex! 👋
            
            Gostaria de cadastrar minha loja no aplicativo para liberar meu acesso ao sistema.
            
            📋 *DADOS DA SOLICITAÇÃO DE CADASTRO:*
            • *Nome da Loja:* $nomeLoja
            • *Dono/Responsável:* $nomeAdmin
            • *Celular/WhatsApp:* $tel
            • *CPF/CNPJ:* ${if (cnpj.isNotEmpty()) cnpj else "Não informado"}
            • *E-mail:* ${if (email.isNotEmpty()) email else "Não informado"}
            
            Aguardando a criação da minha conta e o envio das credenciais de login! 🚀
        """.trimIndent()

        val numeroSuporteWhatsapp = "5561996076814"

        try {
            val uri = Uri.parse("whatsapp://send?phone=$numeroSuporteWhatsapp&text=${java.net.URLEncoder.encode(mensagemComercial, "UTF-8")}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.whatsapp")
            startActivity(intent)
            Toast.makeText(this, "Solicitação enviada ao Suporte Comercial!", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Não foi possível abrir o WhatsApp. Envie para: suporte@lojaflex.com.br", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
