package com.devgabriel.aulatela

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
import com.devgabriel.aulatela.adapter.ContaPagarAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.ContaPagar
import com.devgabriel.aulatela.databinding.ActivityFinanceiroBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class FinanceiroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFinanceiroBinding
    private lateinit var sessionManager: SessionManager
    private val listaContas = mutableListOf<ContaPagar>()
    private lateinit var adapter: ContaPagarAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFinanceiroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainFinanceiro) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvTituloFinanceiro.text = "Contas a Pagar: ${sessionManager.getNomeEmpresa()}"

        setupRecyclerView()
        carregarContasPagar()

        binding.btnVoltarFinanceiro.setOnClickListener { finish() }

        binding.fabNovaConta.setOnClickListener {
            abrirDialogNovaConta()
        }
    }

    private fun setupRecyclerView() {
        adapter = ContaPagarAdapter(listaContas) { conta ->
            darBaixaConta(conta)
        }
        binding.rvContasPagar.layoutManager = LinearLayoutManager(this)
        binding.rvContasPagar.adapter = adapter
    }

    private fun carregarContasPagar() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarContasPagar(sessionManager.getEmpresaId())
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    listaContas.clear()
                    listaContas.addAll(response.body()!!)
                } else {
                    carregarMockContas()
                }
            } catch (e: Exception) {
                carregarMockContas()
            }
            atualizarResumoFinanceiro()
        }
    }

    private fun carregarMockContas() {
        if (listaContas.isEmpty()) {
            listaContas.add(ContaPagar(1, "Fornecedor Ambev - Bebidas", 450.00, "10/09/2026", false))
            listaContas.add(ContaPagar(2, "Conta de Energia Elétrica", 400.00, "15/09/2026", false))
            listaContas.add(ContaPagar(3, "Aluguel da Loja", 1200.00, "01/09/2026", true))
        }
    }

    private fun atualizarResumoFinanceiro() {
        adapter.atualizarLista(listaContas)
        val pendente = listaContas.filter { !it.paga }.sumOf { it.valor }
        val pago = listaContas.filter { it.paga }.sumOf { it.valor }

        binding.tvTotalPendente.text = currencyFormat.format(pendente)
        binding.tvTotalPago.text = currencyFormat.format(pago)
    }

    private fun darBaixaConta(conta: ContaPagar) {
        conta.paga = true
        lifecycleScope.launch {
            try {
                if (conta.id != null) {
                    RetrofitClient.apiService.baixarContaPagar(conta.id)
                }
            } catch (_: Exception) {}
            atualizarResumoFinanceiro()
            Toast.makeText(this@FinanceiroActivity, "Conta marcada como PAGA!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirDialogNovaConta() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val etDesc = EditText(this).apply { hint = "Descrição (ex: Aluguel, Luz)" }
        val etValor = EditText(this).apply {
            hint = "Valor (R$)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        val etVenc = EditText(this).apply { hint = "Data Vencimento (ex: 20/09/2026)" }

        layout.addView(etDesc)
        layout.addView(etValor)
        layout.addView(etVenc)

        AlertDialog.Builder(this)
            .setTitle("Nova Conta - ${sessionManager.getNomeEmpresa()}")
            .setView(layout)
            .setPositiveButton("Cadastrar") { _, _ ->
                val desc = etDesc.text.toString().trim()
                val valNum = etValor.text.toString().toDoubleOrNull() ?: 0.0
                val venc = etVenc.text.toString().trim()

                if (desc.isNotEmpty() && valNum > 0) {
                    val novaConta = ContaPagar((listaContas.size + 1).toLong(), desc, valNum, venc.ifEmpty { "Em aberto" }, false)
                    lifecycleScope.launch {
                        try {
                            RetrofitClient.apiService.salvarContaPagar(novaConta)
                        } catch (_: Exception) {}
                        listaContas.add(0, novaConta)
                        atualizarResumoFinanceiro()
                        Toast.makeText(this@FinanceiroActivity, "Conta cadastrada!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
