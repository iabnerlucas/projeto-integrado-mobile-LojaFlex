package com.devgabriel.aulatela

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devgabriel.aulatela.adapter.ClienteFiadoAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.ClienteFiado
import com.devgabriel.aulatela.databinding.ActivityFiadoBinding
import com.devgabriel.aulatela.databinding.DialogCadastrarFiadoBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class FiadoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFiadoBinding
    private lateinit var adapter: ClienteFiadoAdapter
    private val listaClientes = mutableListOf<ClienteFiado>()
    private var empresaIdSessao: Long = 1L
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFiadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sessionManager = SessionManager(this)
        empresaIdSessao = sessionManager.getEmpresaId().let { if (it <= 0L) 1L else it }

        configurarRecyclerView()
        configurarCliques()
        carregarClientesFiadoDoServidor()
    }

    private fun configurarRecyclerView() {
        adapter = ClienteFiadoAdapter(
            listaClientes,
            onEnviarWhatsappClicked = { clienteSelecionado ->
                Toast.makeText(this, "Enviando termo para ${clienteSelecionado.nome}", Toast.LENGTH_SHORT).show()
                // Aqui iria o intent de Whatsapp já feito anteriormente
            },
            onBaixaClicked = { clienteSelecionado ->
                confirmarQuitacaoDivida(clienteSelecionado)
            }
        )
        binding.rvFiados.layoutManager = LinearLayoutManager(this)
        binding.rvFiados.adapter = adapter
    }

    private fun configurarCliques() {
        binding.btnVoltarFiado.setOnClickListener {
            finish()
        }

        binding.fabNovoFiado.setOnClickListener {
            abrirDialogNovoFiado()
        }
    }

    private fun carregarClientesFiadoDoServidor() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarClientesFiado(empresaIdSessao)
                if (response.isSuccessful && response.body() != null) {
                    listaClientes.clear()
                    listaClientes.addAll(response.body()!!)
                    adapter.atualizarLista(listaClientes)
                    atualizarResumoTotal()
                } else {
                    Toast.makeText(
                        this@FiadoActivity,
                        "Erro ao carregar fiados (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@FiadoActivity,
                    "Sem conexão com o servidor: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun abrirDialogNovoFiado() {
        val dialogBinding = DialogCadastrarFiadoBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnSalvarFiado.setOnClickListener {
            val nome = dialogBinding.etNomeClienteFiado.text.toString().trim()
            val telefone = dialogBinding.etWhatsappCliente.text.toString().trim()
            val valorTexto = dialogBinding.etValorFiado.text.toString().trim().replace(",", ".")
            val vencimento = dialogBinding.etDataVencimentoFiado.text.toString().trim()

            if (nome.isEmpty() || valorTexto.isEmpty()) {
                Toast.makeText(this, "Preencha o nome e o valor da dívida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val valor = valorTexto.toDoubleOrNull()
            if (valor == null || valor <= 0.0) {
                Toast.makeText(this, "Informe um valor válido maior que zero!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoCliente = ClienteFiado(
                nome = nome,
                telefone = telefone,
                valorDivida = valor,
                dataVencimento = vencimento.ifEmpty { "Não informado" },
                status = "PENDENTE",
                empresaId = empresaIdSessao
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.salvarClienteFiado(novoCliente)
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@FiadoActivity, "Cliente cadastrado no banco!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        carregarClientesFiadoDoServidor()
                    } else {
                        Toast.makeText(
                            this@FiadoActivity,
                            "Falha ao salvar no servidor (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        this@FiadoActivity,
                        "Erro de conexão ao salvar: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        dialog.show()
    }

    private fun confirmarQuitacaoDivida(cliente: ClienteFiado) {
        val idCliente = cliente.id ?: return

        AlertDialog.Builder(this)
            .setTitle("Receber Pagamento / Quitar")
            .setMessage("Confirmar o recebimento da dívida de ${cliente.nome}?")
            .setPositiveButton("Confirmar") { _, _ ->
                darBaixaFiadoNoServidor(idCliente)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun darBaixaFiadoNoServidor(clienteId: Long) {
        lifecycleScope.launch {
            try {
                // Aqui usar a rota atualizar ou deletar - usarei deletar como exemplo simplificado
                val response = RetrofitClient.apiService.quitarDividaFiado(clienteId)
                if (response.isSuccessful) {
                    Toast.makeText(this@FiadoActivity, "Dívida quitada com sucesso!", Toast.LENGTH_SHORT).show()
                    carregarClientesFiadoDoServidor()
                } else {
                    Toast.makeText(
                        this@FiadoActivity,
                        "Erro ao dar baixa no servidor (${response.code()})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@FiadoActivity,
                    "Erro de conexão: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun atualizarResumoTotal() {
        val totalReceber = listaClientes
            .filter { it.status != "PAGO" && it.status != "QUITADO" }
            .sumOf { it.valorDivida }

        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        binding.tvTotalFiadoReceber.text = formatoMoeda.format(totalReceber)
    }
}
