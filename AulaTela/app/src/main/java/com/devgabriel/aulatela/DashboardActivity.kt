package com.devgabriel.aulatela

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var sessionManager: SessionManager
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainDashboard) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvTituloDashboard.text = "Dashboard: ${sessionManager.getNomeEmpresa()}"
        binding.btnVoltarDashboard.setOnClickListener { finish() }

        carregarMetricasDoServidor()
    }

    private fun carregarMetricasDoServidor() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.obterMetricasDashboard(sessionManager.getEmpresaId())
                if (response.isSuccessful && response.body() != null) {
                    val m = response.body()!!
                    binding.tvTotalFaturado.text = currencyFormat.format(m.totalFaturado)
                    binding.tvTotalVendas.text = "${m.totalVendasCount} vendas"
                    binding.tvTicketMedio.text = currencyFormat.format(m.ticketMedio)

                    binding.tvFaturamentoDinheiro.text = currencyFormat.format(m.faturamentoDinheiro)
                    binding.tvFaturamentoPix.text = currencyFormat.format(m.faturamentoPix)
                    binding.tvFaturamentoDebito.text = currencyFormat.format(m.faturamentoDebito)
                    binding.tvFaturamentoCredito.text = currencyFormat.format(m.faturamentoCredito)
                } else {
                    exibirZerosMetricas()
                }
            } catch (_: Exception) {
                exibirZerosMetricas()
            }
        }
    }

    private fun exibirZerosMetricas() {
        binding.tvTotalFaturado.text = "R$ 0,00"
        binding.tvTotalVendas.text = "0 vendas"
        binding.tvTicketMedio.text = "R$ 0,00"

        binding.tvFaturamentoDinheiro.text = "R$ 0,00"
        binding.tvFaturamentoPix.text = "R$ 0,00"
        binding.tvFaturamentoDebito.text = "R$ 0,00"
        binding.tvFaturamentoCredito.text = "R$ 0,00"
    }
}
