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
import com.devgabriel.aulatela.adapter.FornecedorAdapter
import com.devgabriel.aulatela.data.api.RetrofitClient
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.data.model.Fornecedor
import com.devgabriel.aulatela.databinding.ActivityFornecedorBinding
import kotlinx.coroutines.launch

class FornecedorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFornecedorBinding
    private lateinit var sessionManager: SessionManager
    private val listaFornecedores = mutableListOf<Fornecedor>()
    private lateinit var adapter: FornecedorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFornecedorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainFornecedor) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvTituloFornecedor.text = "Fornecedores: ${sessionManager.getNomeEmpresa()}"

        setupRecyclerView()
        carregarFornecedores()

        binding.btnVoltarFornecedor.setOnClickListener { finish() }

        binding.fabNovoFornecedor.setOnClickListener {
            abrirDialogNovoFornecedor()
        }
    }

    private fun setupRecyclerView() {
        adapter = FornecedorAdapter(listaFornecedores) { fornecedor ->
            if (!fornecedor.telefone.isNullOrEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${fornecedor.telefone}"))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Telefone não cadastrado", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvFornecedores.layoutManager = LinearLayoutManager(this)
        binding.rvFornecedores.adapter = adapter
    }

    private fun carregarFornecedores() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.listarFornecedores(sessionManager.getEmpresaId())
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    listaFornecedores.clear()
                    listaFornecedores.addAll(response.body()!!)
                }
            } catch (_: Exception) {}
            adapter.atualizarLista(listaFornecedores)
        }
    }

    private fun abrirDialogNovoFornecedor() {
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(48, 32, 48, 16)
        }

        val etNome = EditText(this).apply { hint = "Nome Fantasia (ex: Ambev)" }
        val etCnpj = EditText(this).apply { hint = "CNPJ / Razão Social" }
        val etTel = EditText(this).apply { hint = "Telefone / WhatsApp" }

        layout.addView(etNome)
        layout.addView(etCnpj)
        layout.addView(etTel)

        AlertDialog.Builder(this)
            .setTitle("Novo Fornecedor - ${sessionManager.getNomeEmpresa()}")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val nome = etNome.text.toString().trim()
                val cnpj = etCnpj.text.toString().trim()
                val tel = etTel.text.toString().trim()

                if (nome.isNotEmpty()) {
                    val novoF = Fornecedor((listaFornecedores.size + 1).toLong(), nome, nome, cnpj, tel)
                    lifecycleScope.launch {
                        try {
                            RetrofitClient.apiService.salvarFornecedor(novoF)
                        } catch (_: Exception) {}
                        listaFornecedores.add(0, novoF)
                        adapter.atualizarLista(listaFornecedores)
                        Toast.makeText(this@FornecedorActivity, "Fornecedor '$nome' cadastrado!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
