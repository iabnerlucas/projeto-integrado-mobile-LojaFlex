package com.devgabriel.aulatela

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.devgabriel.aulatela.adapter.UsuariosPendentesAdapter
import com.devgabriel.aulatela.data.local.SessionManager
import com.devgabriel.aulatela.databinding.ActivityUsuariosPendentesBinding
import com.devgabriel.aulatela.viewmodel.UsuariosPendentesState
import com.devgabriel.aulatela.viewmodel.UsuariosPendentesViewModel
import kotlinx.coroutines.launch

class UsuariosPendentesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUsuariosPendentesBinding
    private val viewModel: UsuariosPendentesViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: UsuariosPendentesAdapter
    private var empresaId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsuariosPendentesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        empresaId = sessionManager.getEmpresaId()

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        // Carrega inicialmente
        viewModel.carregarPendentes(empresaId)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = UsuariosPendentesAdapter(
            onAprovarClick = { usuario ->
                viewModel.aprovarUsuario(usuario.id, empresaId)
            },
            onRejeitarClick = { usuario ->
                viewModel.rejeitarUsuario(usuario.id, empresaId)
            }
        )
        binding.rvUsuariosPendentes.layoutManager = LinearLayoutManager(this)
        binding.rvUsuariosPendentes.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UsuariosPendentesState.Idle -> {
                            // Do nothing
                        }
                        is UsuariosPendentesState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is UsuariosPendentesState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (state.usuarios.isEmpty()) {
                                binding.tvEmptyState.visibility = View.VISIBLE
                                binding.rvUsuariosPendentes.visibility = View.GONE
                            } else {
                                binding.tvEmptyState.visibility = View.GONE
                                binding.rvUsuariosPendentes.visibility = View.VISIBLE
                                adapter.setUsuarios(state.usuarios)
                            }
                        }
                        is UsuariosPendentesState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@UsuariosPendentesActivity, state.message, Toast.LENGTH_LONG).show()
                        }
                        is UsuariosPendentesState.ActionSuccess -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(this@UsuariosPendentesActivity, state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}
