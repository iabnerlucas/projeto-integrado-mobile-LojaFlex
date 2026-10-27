package com.devgabriel.aulatela

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainHome) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarPerfilESessao()

        binding.btnSair.setOnClickListener {
            sessionManager.encerrarSessao()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnCardPainelSuporte.setOnClickListener {
            val intent = Intent(this, PainelSuporteActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardVendas.setOnClickListener {
            val intent = Intent(this, PdvActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardFiados.setOnClickListener {
            val intent = Intent(this, FiadoActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardEstoque.setOnClickListener {
            val intent = Intent(this, EstoqueActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardFornecedores.setOnClickListener {
            val intent = Intent(this, FornecedorActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardDashboard.setOnClickListener {
            if (sessionManager.isGerenteLoja() || sessionManager.isSuporte()) {
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Acesso bloqueado: Módulo exclusivo do Gerente", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCardUsuariosPendentes.setOnClickListener {
            val intent = Intent(this, UsuariosPendentesActivity::class.java)
            startActivity(intent)
        }

        binding.btnCardFinanceiro.setOnClickListener {
            if (sessionManager.isGerenteLoja() || sessionManager.isSuporte()) {
                val intent = Intent(this, FinanceiroActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Acesso bloqueado: Módulo exclusivo do Gerente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configurarPerfilESessao() {
        val nomeEmpresa = sessionManager.getNomeEmpresa()
        val nomeOperador = sessionManager.getNomeOperador()
        val perfil = sessionManager.getPerfil()

        binding.tvNomeLoja.text = if (sessionManager.isSuporte()) "LojaFlex Suporte SaaS" else nomeEmpresa
        binding.tvSaudacao.text = "Olá, $nomeOperador"
        binding.tvBadgePerfil.text = perfil.uppercase()

        if (sessionManager.isSuporte()) {
            binding.btnCardPainelSuporte.visibility = View.VISIBLE
            binding.btnCardUsuariosPendentes.visibility = View.GONE // Suporte aprova lojas, gerente aprova usuários
            binding.cardTurno.visibility = View.GONE
            binding.btnCardDashboard.visibility = View.GONE
            binding.btnCardVendas.visibility = View.GONE
            binding.btnCardFiados.visibility = View.GONE
            binding.btnCardEstoque.visibility = View.GONE
            binding.btnCardFornecedores.visibility = View.GONE
            binding.btnCardFinanceiro.visibility = View.GONE
        } else {
            binding.btnCardPainelSuporte.visibility = View.GONE
            binding.cardTurno.visibility = View.VISIBLE
            
            if (sessionManager.isGerenteLoja()) {
                binding.btnCardUsuariosPendentes.visibility = View.VISIBLE
            } else {
                binding.btnCardUsuariosPendentes.visibility = View.GONE
            }
            
            binding.btnCardDashboard.visibility = View.VISIBLE
            binding.btnCardVendas.visibility = View.VISIBLE
            binding.btnCardFiados.visibility = View.VISIBLE
            binding.btnCardEstoque.visibility = View.VISIBLE
            binding.btnCardFornecedores.visibility = View.VISIBLE
            binding.btnCardFinanceiro.visibility = View.VISIBLE

            if (!sessionManager.isGerenteLoja()) {
                binding.btnCardDashboard.alpha = 0.5f
                binding.btnCardFinanceiro.alpha = 0.5f
            } else {
                binding.btnCardDashboard.alpha = 1.0f
                binding.btnCardFinanceiro.alpha = 1.0f
            }
        }
    }
}
