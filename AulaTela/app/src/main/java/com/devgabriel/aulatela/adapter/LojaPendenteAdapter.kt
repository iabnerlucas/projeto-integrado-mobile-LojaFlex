package com.devgabriel.aulatela.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.Empresa
import com.devgabriel.aulatela.databinding.ItemLojaPendenteBinding

class LojaPendenteAdapter(
    private var lojas: List<Empresa>,
    private val onAprovarClicked: (Empresa) -> Unit,
    private val onEnviarWhatsappClicked: (Empresa) -> Unit
) : RecyclerView.Adapter<LojaPendenteAdapter.LojaViewHolder>() {

    inner class LojaViewHolder(val binding: ItemLojaPendenteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LojaViewHolder {
        val binding = ItemLojaPendenteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LojaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LojaViewHolder, position: Int) {
        val loja = lojas[position]
        with(holder.binding) {
            tvNomeLojaSuporte.text = loja.nomeFantasia
            tvCnpjLojaSuporte.text = "CNPJ/CPF: ${loja.cnpjCpf}"
            tvProprietarioLojaSuporte.text = "Resp: ${loja.emailAdmin} (${loja.telefone ?: "Sem tel"})"

            btnAprovarLoja.setOnClickListener { onAprovarClicked(loja) }
            btnEnviarAcessoWhatsapp.setOnClickListener { onEnviarWhatsappClicked(loja) }
        }
    }

    override fun getItemCount(): Int = lojas.size

    fun atualizarLista(novaLista: List<Empresa>) {
        this.lojas = novaLista
        notifyDataSetChanged()
    }
}
