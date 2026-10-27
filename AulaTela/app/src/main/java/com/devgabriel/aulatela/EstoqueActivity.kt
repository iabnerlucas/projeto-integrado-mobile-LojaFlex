package com.devgabriel.aulatela

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devgabriel.aulatela.adapter.ProdutoCatalogoAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.Produto
import com.devgabriel.aulatela.databinding.ActivityEstoqueBinding
import com.devgabriel.aulatela.databinding.DialogAjusteEstoqueBinding
import com.devgabriel.aulatela.databinding.DialogCadastrarProdutoBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class EstoqueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEstoqueBinding
    private lateinit var sessionManager: SessionManager
    private val listaProdutos = mutableListOf<Produto>()
    private lateinit var adapter: ProdutoCatalogoAdapter
    private var barcodeOutputEditText: android.widget.EditText? = null
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    private val barcodeLauncher: ActivityResultLauncher<ScanOptions> =
        registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                barcodeOutputEditText?.setText(result.contents)
                Toast.makeText(this, "Código Lido: ${result.contents}", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEstoqueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainEstoque) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvSubtituloEstoque.text = "Loja: ${sessionManager.getNomeEmpresa()}"

        setupRecyclerView()
        carregarProdutosEstoque()

        binding.btnVoltarEstoque.setOnClickListener { finish() }

        binding.fabNovoProduto.setOnClickListener {
            abrirDialogNovoProduto()
        }

        binding.etBuscaEstoque.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aplicarFiltros()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.chipGroupFiltros.setOnCheckedStateChangeListener { _, _ ->
            aplicarFiltros()
        }
    }

    private fun setupRecyclerView() {
        adapter = ProdutoCatalogoAdapter(listaProdutos, "Editar") { produto ->
            abrirDialogAjusteEstoque(produto)
        }
        binding.rvEstoque.layoutManager = LinearLayoutManager(this)
        binding.rvEstoque.adapter = adapter
    }

    private fun carregarProdutosEstoque() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarProdutosPorEmpresa(sessionManager.getEmpresaId())
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    listaProdutos.clear()
                    listaProdutos.addAll(response.body()!!)
                } else {
                    carregarMockEstoque()
                }
            } catch (e: Exception) {
                carregarMockEstoque()
            }
            atualizarKpisELista()
        }
    }

    private fun carregarMockEstoque() {
        if (listaProdutos.isEmpty()) {
            listaProdutos.add(Produto(id = 1, nome = "Cerveja Heineken 350ml", preco = 6.50, codigoBarras = "789123456789", quantidadeEstoque = 48))
            listaProdutos.add(Produto(id = 2, nome = "Refrigerante Coca-Cola 2L", preco = 8.00, codigoBarras = "789123456790", quantidadeEstoque = 5))
            listaProdutos.add(Produto(id = 3, nome = "Água Mineral 500ml", preco = 2.50, codigoBarras = "789123456791", quantidadeEstoque = 100))
            listaProdutos.add(Produto(id = 4, nome = "Salgadinho Elma Chips 100g", preco = 12.00, codigoBarras = "789123456792", quantidadeEstoque = 0))
            listaProdutos.add(Produto(id = 5, nome = "Energético Red Bull 250ml", preco = 11.50, codigoBarras = "789123456793", quantidadeEstoque = 30))
        }
    }

    private fun atualizarKpisELista() {
        val valorTotalEstoque = listaProdutos.sumOf { it.preco * it.quantidadeEstoque }
        val alertasCount = listaProdutos.count { it.quantidadeEstoque <= 10 }

        binding.tvValorTotalEstoque.text = currencyFormat.format(valorTotalEstoque)
        binding.tvAlertasEstoqueCount.text = "$alertasCount com poucas un."

        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val query = binding.etBuscaEstoque.text.toString().trim()
        val checkedChipId = binding.chipGroupFiltros.checkedChipId

        var filtrados = listaProdutos.toList()

        if (checkedChipId == R.id.chipBaixoEstoque) {
            filtrados = filtrados.filter { it.quantidadeEstoque <= 10 }
        } else if (checkedChipId == R.id.chipNormal) {
            filtrados = filtrados.filter { it.quantidadeEstoque > 10 }
        }

        if (query.isNotEmpty()) {
            filtrados = filtrados.filter {
                it.nome.contains(query, ignoreCase = true) ||
                (it.codigoBarras != null && it.codigoBarras.contains(query))
            }
        }

        adapter.atualizarLista(filtrados)
        binding.tvTotalEstoqueCount.text = "${filtrados.size} de ${listaProdutos.size} itens"
    }

    private fun abrirDialogAjusteEstoque(produto: Produto) {
        val dialogBinding = DialogAjusteEstoqueBinding.inflate(layoutInflater)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.getRoot())

        dialogBinding.etEditNomeProduto.setText(produto.nome)
        dialogBinding.etEditPrecoCustoProduto.setText(produto.preco.toString()) // Mock value, precoCusto removed
        dialogBinding.etEditPrecoProduto.setText(produto.preco.toString())
        dialogBinding.etEditCodigoBarra.setText(produto.codigoBarras ?: "")
        dialogBinding.etNovaQuantidade.setText(produto.quantidadeEstoque.toString())

        dialogBinding.btnEscanearEditCodigo.setOnClickListener {
            barcodeOutputEditText = dialogBinding.etEditCodigoBarra
            val options = ScanOptions().apply {
                setPrompt("Aponte a câmera para o Código de Barras")
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        var quantTemp = produto.quantidadeEstoque

        fun atualizarEditTemp() {
            if (quantTemp < 0) quantTemp = 0
            dialogBinding.etNovaQuantidade.setText(quantTemp.toString())
        }

        dialogBinding.btnAdd1.setOnClickListener { quantTemp += 1; atualizarEditTemp() }
        dialogBinding.btnAdd5.setOnClickListener { quantTemp += 5; atualizarEditTemp() }
        dialogBinding.btnAdd10.setOnClickListener { quantTemp += 10; atualizarEditTemp() }
        dialogBinding.btnSub1.setOnClickListener { quantTemp -= 1; atualizarEditTemp() }

        dialogBinding.btnConfirmarAjuste.setOnClickListener {
            val novoNome = dialogBinding.etEditNomeProduto.text.toString().trim()
            val novoPrecoVenda = dialogBinding.etEditPrecoProduto.text.toString().toDoubleOrNull() ?: produto.preco
            val novoCodBarra = dialogBinding.etEditCodigoBarra.text.toString().trim()
            val novaQtd = dialogBinding.etNovaQuantidade.text.toString().toIntOrNull() ?: quantTemp

            if (novoNome.isEmpty()) {
                dialogBinding.etEditNomeProduto.error = "Nome não pode ficar vazio"
                return@setOnClickListener
            }

            val prodIndex = listaProdutos.indexOfFirst { it.id == produto.id }
            if (prodIndex != -1) {
                val prodAtualizado = listaProdutos[prodIndex].copy(
                    nome = novoNome,
                    preco = novoPrecoVenda,
                    codigoBarras = novoCodBarra.ifEmpty { null },
                    quantidadeEstoque = novaQtd
                )
                listaProdutos[prodIndex] = prodAtualizado

                lifecycleScope.launch {
                    try {
                        RetrofitClient.apiService.ajustarEstoque(prodAtualizado.id ?: 0L, novaQtd)
                    } catch (_: Exception) {}
                    atualizarKpisELista()
                    Toast.makeText(this@EstoqueActivity, "Produto '${prodAtualizado.nome}' atualizado!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun abrirDialogNovoProduto() {
        val dialogBinding = DialogCadastrarProdutoBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.getRoot())
            .create()

        dialogBinding.btnEscanearCodigoBarra.setOnClickListener {
            barcodeOutputEditText = dialogBinding.etCodigoBarra
            val options = ScanOptions().apply {
                setPrompt("Aponte a câmera para o Código de Barras")
                setBeepEnabled(true)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        dialogBinding.btnSalvarProduto.setOnClickListener {
            val nome = dialogBinding.etNomeProduto.text.toString().trim()
            val precoCusto = dialogBinding.
            etPrecoCustoProduto.text.toString().toDoubleOrNull() ?: 0.0
            val precoVenda = dialogBinding.etPrecoProduto.text.toString().toDoubleOrNull() ?: 0.0
            val estoque = dialogBinding.etEstoqueInicial.text.toString().toIntOrNull() ?: 0
            val codBarra = dialogBinding.etCodigoBarra.text.toString().trim()

            if (nome.isEmpty() || precoVenda <= 0.0) {
                Toast.makeText(this, "Preencha o nome e preço de venda do produto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val novoProd = Produto(
                id = (listaProdutos.size + 1).toLong(),
                nome = nome,
                preco = precoVenda,
                codigoBarras = codBarra.ifEmpty { null },
                quantidadeEstoque = estoque
            )

            lifecycleScope.launch {
                try {
                    RetrofitClient.apiService.cadastrarProduto(novoProd)
                } catch (_: Exception) {}
                listaProdutos.add(0, novoProd)
                atualizarKpisELista()
                Toast.makeText(this@EstoqueActivity, "Produto adicionado ao estoque!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
