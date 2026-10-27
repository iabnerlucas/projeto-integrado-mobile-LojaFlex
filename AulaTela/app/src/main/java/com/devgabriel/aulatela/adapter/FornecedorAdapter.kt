package com.devgabriel.aulatela.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.Fornecedor
import com.devgabriel.aulatela.databinding.ItemFornecedorBinding

class FornecedorAdapter(
    private var fornecedores: List<Fornecedor>,
    private val onContatoClicked: (Fornecedor) -> Unit
) : RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>() {

    inner class FornecedorViewHolder(val binding: ItemFornecedorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FornecedorViewHolder {
        val binding = ItemFornecedorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FornecedorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FornecedorViewHolder, position: Int) {
        val f = fornecedores[position]
        with(holder.binding) {
            tvNomeFornecedor.text = f.nomeFantasia
            tvCnpjFornecedor.text = "CNPJ: ${f.cnpj ?: "Não informado"}"
            tvTelefoneFornecedor.text = "Tel: ${f.telefone ?: "Não informado"}"

            btnContatoFornecedor.setOnClickListener {
                onContatoClicked(f)
            }
        }
    }

    override fun getItemCount(): Int = fornecedores.size

    fun atualizarLista(novaLista: List<Fornecedor>) {
        this.fornecedores = novaLista
        notifyDataSetChanged()
    }
}
