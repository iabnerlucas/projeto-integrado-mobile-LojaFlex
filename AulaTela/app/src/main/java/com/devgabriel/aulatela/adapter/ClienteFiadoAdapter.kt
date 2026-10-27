package com.devgabriel.aulatela.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.ClienteFiado
import com.devgabriel.aulatela.databinding.ItemClienteFiadoBinding
import java.text.NumberFormat
import java.util.Locale

class ClienteFiadoAdapter(
    private var fiados: List<ClienteFiado>,
    private val onEnviarWhatsappClicked: (ClienteFiado) -> Unit,
    private val onBaixaClicked: (ClienteFiado) -> Unit
) : RecyclerView.Adapter<ClienteFiadoAdapter.FiadoViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    inner class FiadoViewHolder(val binding: ItemClienteFiadoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiadoViewHolder {
        val binding = ItemClienteFiadoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FiadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FiadoViewHolder, position: Int) {
        val f = fiados[position]
        with(holder.binding) {
            tvNomeClienteFiado.text = f.nome
            tvCpfClienteFiado.text = "Telefone: ${f.telefone.ifEmpty { "Não informado" }}"
            tvItensFiado.text = "Status: ${f.status}"
            tvValorDevido.text = currencyFormat.format(f.valorDivida)
            tvLimiteCredito.text = "Vence em: ${f.dataVencimento}"
            tvVencimentoFiado.text = "Vence em: ${f.dataVencimento}"

            btnEnviarWhatsapp.setOnClickListener { onEnviarWhatsappClicked(f) }
            btnDarBaixaFiado.setOnClickListener { onBaixaClicked(f) }
        }
    }

    override fun getItemCount(): Int = fiados.size

    fun atualizarLista(novaLista: List<ClienteFiado>) {
        this.fiados = novaLista
        notifyDataSetChanged()
    }
}
