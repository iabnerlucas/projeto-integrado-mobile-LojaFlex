package com.devgabriel.aulatela.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.ContaPagar
import com.devgabriel.aulatela.databinding.ItemContaPagarBinding
import java.text.NumberFormat
import java.util.Locale

class ContaPagarAdapter(
    private var contas: List<ContaPagar>,
    private val onPagarClicked: (ContaPagar) -> Unit
) : RecyclerView.Adapter<ContaPagarAdapter.ContaViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    inner class ContaViewHolder(val binding: ItemContaPagarBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContaViewHolder {
        val binding = ItemContaPagarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContaViewHolder, position: Int) {
        val conta = contas[position]
        with(holder.binding) {
            tvDescricaoConta.text = conta.descricao
            tvVencimentoConta.text = "Vencimento: ${conta.dataVencimento}"
            tvValorConta.text = currencyFormat.format(conta.valor)

            if (conta.paga) {
                btnMarcarPaga.text = "PAGO"
                btnMarcarPaga.isEnabled = false
                btnMarcarPaga.setBackgroundColor(Color.GRAY)
            } else {
                btnMarcarPaga.text = "Pagar"
                btnMarcarPaga.isEnabled = true
                btnMarcarPaga.setOnClickListener {
                    onPagarClicked(conta)
                }
            }
        }
    }

    override fun getItemCount(): Int = contas.size

    fun atualizarLista(novaLista: List<ContaPagar>) {
        this.contas = novaLista
        notifyDataSetChanged()
    }
}
