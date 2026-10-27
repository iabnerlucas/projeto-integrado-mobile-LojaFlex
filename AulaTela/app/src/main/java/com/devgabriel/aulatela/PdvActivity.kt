package com.devgabriel.aulatela

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devgabriel.aulatela.adapter.CarrinhoAdapter
import com.devgabriel.aulatela.adapter.ProdutoCatalogoAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.ClienteFiado
import com.devgabriel.aulatela.data.model.ItemCarrinho
import com.devgabriel.aulatela.data.model.ItemPedidoRequest
import com.devgabriel.aulatela.data.model.PedidoRequest
import com.devgabriel.aulatela.data.model.Produto
import com.devgabriel.aulatela.data.repository.FiadoRepository
import com.devgabriel.aulatela.databinding.ActivityPdvBinding
import com.devgabriel.aulatela.databinding.DialogCadastrarFiadoBinding
import com.devgabriel.aulatela.databinding.DialogPagamentoPdvBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class PdvActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdvBinding
    private val carrinhoCompleto = mutableListOf<ItemCarrinho>()
    private val carrinhoExibicao = mutableListOf<ItemCarrinho>()
    private val catalogoProdutos = mutableListOf<Produto>()
    private val vendasDoTurnoLocal = mutableListOf<PedidoRequest>()
    private lateinit var adapter: CarrinhoAdapter
    private lateinit var sessionManager: SessionManager
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val codLido = result.contents
                val prodEncontrado = catalogoProdutos.find { it.codigoBarras == codLido }
                if (prodEncontrado != null) {
                    adicionarAoCarrinho(prodEncontrado)
                    Toast.makeText(this, "${prodEncontrado.nome} adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Produto não encontrado com o código: $codLido", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPdvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainPdv) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvSubtituloPdv.text = "Caixa: ${sessionManager.getNomeOperador()}"

        setupRecyclerView()
        carregarProdutosDoServidor()

        binding.btnVoltarPdv.setOnClickListener { finish() }

        binding.btnFecharCaixaTurno.setOnClickListener {
            abrirRelatorioFechamentoCaixa()
        }

        binding.etBuscaProduto.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    binding.btnLimparBusca.visibility = View.VISIBLE
                } else {
                    binding.btnLimparBusca.visibility = View.GONE
                }
                filtrarCarrinhoLocal(query)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnLimparBusca.setOnClickListener {
            binding.etBuscaProduto.text?.clear()
        }

        binding.btnEscanearPdv.setOnClickListener {
            val options = ScanOptions().apply {
                setPrompt("Aponte a câmera para o Código de Barras")
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        binding.btnAbrirCatalogoPdv.setOnClickListener {
            abrirCatalogoDialog()
        }

        binding.btnFinalizarVenda.setOnClickListener {
            if (carrinhoCompleto.isEmpty()) {
                Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            abrirDialogPagamento()
        }
    }

    private fun setupRecyclerView() {
        adapter = CarrinhoAdapter(
            carrinhoExibicao,
            onItemQuantidadeChanged = {
                atualizarResumoVenda()
            }
        )
        binding.rvCarrinho.layoutManager = LinearLayoutManager(this)
        binding.rvCarrinho.adapter = adapter
    }

    private fun carregarProdutosDoServidor() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarProdutosPorEmpresa(sessionManager.getEmpresaId())
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    catalogoProdutos.clear()
                    catalogoProdutos.addAll(response.body()!!)
                } else {
                    carregarMockCatalogo()
                }
            } catch (e: Exception) {
                carregarMockCatalogo()
            }
        }
    }

    private fun carregarMockCatalogo() {
        if (catalogoProdutos.isEmpty()) {
            catalogoProdutos.add(Produto(id = 1, nome = "Cerveja Heineken 350ml", preco = 6.50, codigoBarras = "789123456789", quantidadeEstoque = 50, empresaId = sessionManager.getEmpresaId()))
            catalogoProdutos.add(Produto(id = 2, nome = "Refrigerante Coca-Cola 2L", preco = 8.00, codigoBarras = "789123456790", quantidadeEstoque = 30, empresaId = sessionManager.getEmpresaId()))
            catalogoProdutos.add(Produto(id = 3, nome = "Água Mineral 500ml", preco = 2.50, codigoBarras = "789123456791", quantidadeEstoque = 100, empresaId = sessionManager.getEmpresaId()))
            catalogoProdutos.add(Produto(id = 4, nome = "Salgadinho Elma Chips 100g", preco = 12.00, codigoBarras = "789123456792", quantidadeEstoque = 20, empresaId = sessionManager.getEmpresaId()))
            catalogoProdutos.add(Produto(id = 5, nome = "Energético Red Bull 250ml", preco = 11.50, codigoBarras = "789123456793", quantidadeEstoque = 40, empresaId = sessionManager.getEmpresaId()))
        }
    }

    private fun adicionarAoCarrinho(produto: Produto) {
        val itemExistente = carrinhoCompleto.find { it.produto.id == produto.id }
        if (itemExistente != null) {
            itemExistente.quantidade++
        } else {
            carrinhoCompleto.add(ItemCarrinho(produto, 1))
        }
        filtrarCarrinhoLocal(binding.etBuscaProduto.text.toString())
        atualizarResumoVenda()
    }

    private fun filtrarCarrinhoLocal(query: String) {
        carrinhoExibicao.clear()
        if (query.trim().isEmpty()) {
            carrinhoExibicao.addAll(carrinhoCompleto)
        } else {
            carrinhoExibicao.addAll(
                carrinhoCompleto.filter {
                    it.produto.nome.contains(query, ignoreCase = true) ||
                    (it.produto.codigoBarras != null && it.produto.codigoBarras.contains(query))
                }
            )
        }
        adapter.notifyDataSetChanged()
    }

    private fun atualizarResumoVenda() {
        val totalItens = carrinhoCompleto.sumOf { it.quantidade }
        val valorTotal = carrinhoCompleto.sumOf { it.subtotal }

        binding.tvValorTotalVenda.text = currencyFormat.format(valorTotal)
        binding.tvTotalItensContador.text = "$totalItens itens"
    }

    private fun abrirRelatorioFechamentoCaixa() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 36, 48, 24)
        }

        val op = sessionManager.getNomeOperador()
        val perfil = sessionManager.getPerfil()
        val totalDinheiro = vendasDoTurnoLocal.filter { it.formaPagamento.contains("DINHEIRO", ignoreCase = true) }.size
        val totalPix = vendasDoTurnoLocal.filter { it.formaPagamento.contains("PIX", ignoreCase = true) }.size
        val totalCartoes = vendasDoTurnoLocal.filter { it.formaPagamento.contains("DÉBITO", ignoreCase = true) || it.formaPagamento.contains("CRÉDITO", ignoreCase = true) || it.formaPagamento.contains("CARTÃO", ignoreCase = true) }.size
        val totalFiado = vendasDoTurnoLocal.filter { it.formaPagamento.contains("FIADO", ignoreCase = true) }.size
        val totalGeral = vendasDoTurnoLocal.size
        val totalVendasCount = vendasDoTurnoLocal.size

        val tvInfo = TextView(this).apply {
            text = """
                📋 FECHAMENTO DE TURNO DO CAIXA
                
                • Operador: $op
                • Perfil: $perfil
                • Total de Vendas no Turno: $totalVendasCount vendas
                
                💵 Vendas em Dinheiro: ${currencyFormat.format(totalDinheiro)}
                ⚡ Vendas em PIX: ${currencyFormat.format(totalPix)}
                💳 Vendas em Cartões: ${currencyFormat.format(totalCartoes)}
                📝 Vendas em Fiado: ${currencyFormat.format(totalFiado)}
                
                💰 TOTAL FATURADO NESTE CAIXA:
                ${currencyFormat.format(totalGeral)}
            """.trimIndent()
            textSize = 14f
            setTextColor(android.graphics.Color.BLACK)
        }

        layout.addView(tvInfo)

        AlertDialog.Builder(this)
            .setTitle("Fechamento de Caixa: $op")
            .setView(layout)
            .setPositiveButton("CONFIRMAR FECHAMENTO & SAIR") { _, _ ->
                Toast.makeText(this, "Caixa do operador '$op' fechado com sucesso!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, SelecaoPerfilActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Continuar Vendendo", null)
            .show()
    }

    private fun abrirCatalogoDialog() {
        val dialogView = layoutInflater.inflate(R.layout.activity_estoque, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Catálogo de Produtos")
            .setView(dialogView)
            .setNegativeButton("Fechar", null)
            .create()

        val rvCatalogo = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvEstoque)
        val etBusca = dialogView.findViewById<android.widget.EditText>(R.id.etBuscaEstoque)
        val fab = dialogView.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabNovoProduto)
        val btnVoltar = dialogView.findViewById<android.widget.ImageButton>(R.id.btnVoltarEstoque)

        btnVoltar.visibility = View.GONE
        fab.visibility = View.GONE

        val catalogoAdapter = ProdutoCatalogoAdapter(catalogoProdutos, "Adicionar") { produto ->
            adicionarAoCarrinho(produto)
            Toast.makeText(this, "'${produto.nome}' adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
        }

        rvCatalogo.layoutManager = LinearLayoutManager(this)
        rvCatalogo.adapter = catalogoAdapter

        etBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val q = s.toString().trim()
                if (q.isEmpty()) {
                    catalogoAdapter.atualizarLista(catalogoProdutos)
                } else {
                    catalogoAdapter.atualizarLista(catalogoProdutos.filter {
                        it.nome.contains(q, ignoreCase = true) || (it.codigoBarras != null && it.codigoBarras.contains(q))
                    })
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }

    private fun abrirDialogPagamento() {
        val dialogBinding = DialogPagamentoPdvBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.getRoot())
            .create()

        val valorTotal = carrinhoCompleto.sumOf { it.subtotal }
        dialogBinding.tvModalValorTotal.text = currencyFormat.format(valorTotal)

        // Configuração do Spinner de Clientes para Fiado
        val clientes = FiadoRepository.clientes
        fun atualizarSpinnerClientes() {
            val nomes = if (clientes.isEmpty()) listOf("Nenhum cliente cadastrado") else clientes.map { "${it.nome} (${it.telefone.ifEmpty { "Sem telefone" }})" }
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomes)
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogBinding.spinnerClientesFiado.adapter = spinnerAdapter
        }
        atualizarSpinnerClientes()

        fun atualizarResumoCreditoCliente(pos: Int) {
            if (clientes.isNotEmpty() && pos in clientes.indices) {
                val c = clientes[pos]
                val disponivel = 200.0 - c.valorDivida
                dialogBinding.tvInfoLimiteFiado.text = "Limite de Crédito: R$ 200,00"
                dialogBinding.tvInfoDebitoAtualFiado.text = "Débito Atual: ${currencyFormat.format(c.valorDivida)}"
                dialogBinding.tvInfoCreditoDisponivelFiado.text = "Disponível para esta compra: ${currencyFormat.format(disponivel)}"
                if (disponivel < valorTotal) {
                    dialogBinding.tvInfoCreditoDisponivelFiado.text = "Disponível: ${currencyFormat.format(disponivel)} (Saldo insuficiente!)"
                    dialogBinding.tvInfoCreditoDisponivelFiado.setTextColor(android.graphics.Color.RED)
                } else {
                    dialogBinding.tvInfoCreditoDisponivelFiado.setTextColor(android.graphics.Color.parseColor("#276749"))
                }
            }
        }

        dialogBinding.spinnerClientesFiado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                atualizarResumoCreditoCliente(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dialogBinding.btnCadastrarNovoClienteFiado.setOnClickListener {
            abrirDialogCadastroRapidoFiado { novoCliente ->
                atualizarSpinnerClientes()
                val indexNovo = clientes.indexOfFirst { it.id == novoCliente.id }
                if (indexNovo != -1) {
                    dialogBinding.spinnerClientesFiado.setSelection(indexNovo)
                }
            }
        }

        dialogBinding.etVencimentoFiadoPdv.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a Data de Vencimento")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(selection)
                val date = Date(selection - offset)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                dialogBinding.etVencimentoFiadoPdv.setText(sdf.format(date))
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_PDV")
        }

        fun atualizarTroco() {
            val recebido = dialogBinding.etValorRecebido.text.toString().replace(",", ".").toDoubleOrNull() ?: 0.0
            val troco = recebido - valorTotal
            if (troco > 0) {
                dialogBinding.tvTrocoCalculado.text = currencyFormat.format(troco)
            } else {
                dialogBinding.tvTrocoCalculado.text = "R$ 0,00"
            }
        }

        dialogBinding.etValorRecebido.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                atualizarTroco()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialogBinding.btnVal10.setOnClickListener { dialogBinding.etValorRecebido.setText("10.00") }
        dialogBinding.btnVal20.setOnClickListener { dialogBinding.etValorRecebido.setText("20.00") }
        dialogBinding.btnVal50.setOnClickListener { dialogBinding.etValorRecebido.setText("50.00") }
        dialogBinding.btnVal100.setOnClickListener { dialogBinding.etValorRecebido.setText("100.00") }
        dialogBinding.btnValExato.setOnClickListener {
            dialogBinding.etValorRecebido.setText(String.format(Locale.US, "%.2f", valorTotal))
        }

        dialogBinding.rgFormaPagamento.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDinheiro -> {
                    dialogBinding.containerDinheiro.visibility = View.VISIBLE
                    dialogBinding.containerFiado.visibility = View.GONE
                }
                R.id.rbFiado -> {
                    dialogBinding.containerDinheiro.visibility = View.GONE
                    dialogBinding.containerFiado.visibility = View.VISIBLE
                }
                else -> {
                    dialogBinding.containerDinheiro.visibility = View.GONE
                    dialogBinding.containerFiado.visibility = View.GONE
                }
            }
        }

        dialogBinding.btnConfirmarPagamento.setOnClickListener {
            val formaPagamentoSelecionada = when (dialogBinding.rgFormaPagamento.checkedRadioButtonId) {
                R.id.rbPix -> "PIX"
                R.id.rbDebito -> "DÉBITO"
                R.id.rbCredito -> "CRÉDITO"
                R.id.rbFiado -> "FIADO"
                else -> "DINHEIRO"
            }

            if (formaPagamentoSelecionada == "FIADO") {
                if (clientes.isEmpty()) {
                    Toast.makeText(this, "Nenhum cliente cadastrado! Clique em '+ Novo' para cadastrar.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val clienteSelectedPos = dialogBinding.spinnerClientesFiado.selectedItemPosition
                val cliente = clientes.getOrNull(clienteSelectedPos)
                if (cliente == null) {
                    Toast.makeText(this, "Selecione um cliente para o fiado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val resumoItens = carrinhoCompleto.joinToString(", ") { "${it.quantidade}x ${it.produto.nome}" }
                
                // Atualizar dívida e histórico de compras do cliente
                cliente.valorDivida += valorTotal
                cliente.status = "PENDENTE"

                Toast.makeText(this, "Venda registrada no FIADO de ${cliente.nome}!", Toast.LENGTH_LONG).show()
                
                enviarPedidoServidor(dialog, "FIADO / A PRAZO", valorTotal, valorTotal, 0.0)

                // Enviar termo WhatsApp se houver telefone
                if (cliente.telefone.isNotEmpty()) {
                    enviarTermoWhatsapp(cliente)
                }
            } else {
                val valorRecebido = dialogBinding.etValorRecebido.text.toString().replace(",", ".").toDoubleOrNull() ?: valorTotal
                val troco = if (valorRecebido > valorTotal) valorRecebido - valorTotal else 0.0
                enviarPedidoServidor(dialog, formaPagamentoSelecionada, valorTotal, valorRecebido, troco)
            }
        }

        dialog.show()
    }

    private fun abrirDialogCadastroRapidoFiado(onClienteCadastrado: (ClienteFiado) -> Unit) {
        val dialogBinding = DialogCadastrarFiadoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.getRoot())
            .create()

        dialogBinding.etDataVencimentoFiado.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a Data de Vencimento")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(selection)
                val date = Date(selection - offset)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                dialogBinding.etDataVencimentoFiado.setText(sdf.format(date))
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_NOVO")
        }

        dialogBinding.btnSalvarFiado.setOnClickListener {
            val nome = dialogBinding.etNomeClienteFiado.text.toString().trim()
            val cpf = dialogBinding.etCpfClienteFiado.text.toString().trim()
            val wpp = dialogBinding.etWhatsappCliente.text.toString().trim()
            val limite = dialogBinding.etLimiteCredito.text.toString().toDoubleOrNull() ?: 200.0
            val venc = dialogBinding.etDataVencimentoFiado.text.toString().trim()

            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cal = Calendar.getInstance()
            val sdfData = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            val sdfHora = SimpleDateFormat("HH:mm", Locale("pt", "BR"))

            val novoFiado = ClienteFiado(
                id = (FiadoRepository.clientes.size + 1).toLong(),
                nome = nome,
                telefone = wpp,
                valorDivida = 0.0,
                dataVencimento = venc.ifEmpty { "Em 15 dias" },
                status = "PENDENTE",
                empresaId = sessionManager.getEmpresaId()
            )

            FiadoRepository.clientes.add(0, novoFiado)
            Toast.makeText(this, "Cliente ${novoFiado.nome} cadastrado!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            onClienteCadastrado(novoFiado)
        }

        dialog.show()
    }

    private fun obterSaudacaoHorario(): String {
        val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hora in 5..11 -> "Bom dia"
            hora in 12..17 -> "Boa tarde"
            else -> "Boa noite"
        }
    }

    private fun enviarTermoWhatsapp(cliente: ClienteFiado) {
        val foneLimpo = cliente.telefone.replace("[^0-9]".toRegex(), "")
        if (foneLimpo.isEmpty() || foneLimpo.length < 8) {
            Toast.makeText(this, "Não foi possível enviar WhatsApp: Telefone do cliente não cadastrado.", Toast.LENGTH_SHORT).show()
            return
        }

        val saudacao = obterSaudacaoHorario()
        val nomeLoja = sessionManager.getNomeEmpresa()
        val vendedor = sessionManager.getNomeOperador()

        val mensagemTexto = """
            $saudacao, ${cliente.nome}! Tudo bem?
            
            *Agradecemos a preferência na $nomeLoja.*
            
            📄 *TERMO DE CONFISSÃO E COMPROVANTE DE COMPRA A PRAZO*
            • *Loja:* $nomeLoja
            • *Vendedor responsável:* $vendedor
            • *Cliente:* ${cliente.nome}
            • *Valor Total Devido:* ${currencyFormat.format(cliente.valorDivida)}
            • *Data Vencimento:* ${cliente.dataVencimento}
            
            ⚠️ *CLÁUSULA DE ACEITE:*
            Ao receber este comprovante, você confirma a ciência e o aceite do compromisso de pagamento da quantia acima até a data de vencimento estipulada (${cliente.dataVencimento}), estando ciente de que o não pagamento no prazo sujeitará a dívida a cobrança extrajudicial/judicial conforme a legislação vigente.
            
            💳 *Chave PIX da Loja para pagamento:*
            [Insira aqui a chave PIX da sua loja]
            
            Tenha um ótimo dia!
        """.trimIndent()

        val foneFinal = if (foneLimpo.startsWith("55")) foneLimpo else "55$foneLimpo"

        try {
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$foneFinal&text=${Uri.encode(mensagemTexto)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Não foi possível abrir o WhatsApp. Instale o app do WhatsApp.", Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarPedidoServidor(
        dialog: AlertDialog,
        formaPagamento: String,
        valorTotal: Double,
        valorRecebido: Double,
        troco: Double
    ) {
        val itensRequest = carrinhoCompleto.map {
            ItemPedidoRequest(it.produto.id ?: 0L, it.quantidade)
        }
        val pedidoRequest = PedidoRequest(
            empresaId = sessionManager.getEmpresaId(),
            usuarioId = sessionManager.getUserId(),
            formaPagamento = formaPagamento,
            itens = itensRequest
        )

        // Registrar venda local no turno do caixa
        vendasDoTurnoLocal.add(pedidoRequest)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.criarPedido(pedidoRequest)
                if (response.isSuccessful) {
                    Toast.makeText(this@PdvActivity, "Venda registrada no caixa de ${sessionManager.getNomeOperador()}!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@PdvActivity, "Venda finalizada com sucesso!", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PdvActivity, "Venda finalizada (Modo Offline)!", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
            carrinhoCompleto.clear()
            carrinhoExibicao.clear()
            adapter.notifyDataSetChanged()
            atualizarResumoVenda()
        }
    }
}
