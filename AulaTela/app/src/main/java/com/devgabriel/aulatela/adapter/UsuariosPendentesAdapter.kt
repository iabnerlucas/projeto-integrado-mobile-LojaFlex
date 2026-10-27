package com.devgabriel.aulatela.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.UsuarioResponse
import com.devgabriel.aulatela.databinding.ItemUsuarioPendenteBinding

class UsuariosPendentesAdapter(
    private val onAprovarClick: (UsuarioResponse) -> Unit,
    private val onRejeitarClick: (UsuarioResponse) -> Unit
) : RecyclerView.Adapter<UsuariosPendentesAdapter.ViewHolder>() {

    private val usuarios = mutableListOf<UsuarioResponse>()

    fun setUsuarios(novosUsuarios: List<UsuarioResponse>) {
        usuarios.clear()
        usuarios.addAll(novosUsuarios)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUsuarioPendenteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usuarios[position])
    }

    override fun getItemCount(): Int = usuarios.size

    inner class ViewHolder(private val binding: ItemUsuarioPendenteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(usuario: UsuarioResponse) {
            binding.tvNomeUsuario.text = usuario.nome
            binding.tvLoginUsuario.text = usuario.login
            binding.tvPerfilUsuario.text = usuario.perfil

            binding.btnAprovar.setOnClickListener {
                onAprovarClick(usuario)
            }

            binding.btnRejeitar.setOnClickListener {
                onRejeitarClick(usuario)
            }
        }
    }
}
