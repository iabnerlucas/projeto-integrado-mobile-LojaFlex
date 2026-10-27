package com.devgabriel.aulatela.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.ItemCarrinho
import com.devgabriel.aulatela.databinding.ItemCarrinhoPdvBinding
import java.text.NumberFormat
import java.util.Locale

class CarrinhoAdapter(
    private val itens: MutableList<ItemCarrinho>,
    private val onItemQuantidadeChanged: () -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    inner class CarrinhoViewHolder(val binding: ItemCarrinhoPdvBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCarrinhoPdvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val item = itens[position]
        with(holder.binding) {
            tvNomeItem.text = item.produto.nome
            tvPrecoUnitario.text = "${currencyFormat.format(item.produto.preco)} un."
            tvSubtotalItem.text = currencyFormat.format(item.subtotal)
            tvQuantidade.text = item.quantidade.toString()

            btnAumentar.setOnClickListener {
                item.quantidade++
                tvQuantidade.text = item.quantidade.toString()
                tvSubtotalItem.text = currencyFormat.format(item.subtotal)
                onItemQuantidadeChanged()
            }

            btnDiminuir.setOnClickListener {
                if (item.quantidade > 1) {
                    item.quantidade--
                    tvQuantidade.text = item.quantidade.toString()
                    tvSubtotalItem.text = currencyFormat.format(item.subtotal)
                } else {
                    val currentPos = holder.adapterPosition
                    if (currentPos != RecyclerView.NO_POSITION) {
                        itens.removeAt(currentPos)
                        notifyItemRemoved(currentPos)
                    }
                }
                onItemQuantidadeChanged()
            }
        }
    }

    override fun getItemCount(): Int = itens.size
}
