package com.devgabriel.aulatela.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devgabriel.aulatela.data.model.Produto
import com.devgabriel.aulatela.databinding.ItemProdutoCatalogoBinding
import java.text.NumberFormat
import java.util.Locale

class ProdutoCatalogoAdapter(
    private var produtos: List<Produto>,
    private val textoBotao: String = "+ Adicionar",
    private val onProdutoClicked: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoCatalogoAdapter.ProdutoViewHolder>() {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    inner class ProdutoViewHolder(val binding: ItemProdutoCatalogoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val binding = ItemProdutoCatalogoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]
        with(holder.binding) {
            tvNomeProduto.text = produto.nome
            tvPrecoProduto.text = currencyFormat.format(produto.preco)
            tvEstoqueProduto.text = "Estoque: ${produto.quantidadeEstoque} un"
            btnAcaoProduto.text = textoBotao

            when {
                produto.quantidadeEstoque <= 0 -> {
                    tvStatusBadge.text = "ESGOTADO"
                    tvStatusBadge.setBackgroundColor(Color.parseColor("#E53935"))
                }
                produto.quantidadeEstoque <= 10 -> {
                    tvStatusBadge.text = "BAIXO"
                    tvStatusBadge.setBackgroundColor(Color.parseColor("#FB8C00"))
                }
                else -> {
                    tvStatusBadge.text = "NORMAL"
                    tvStatusBadge.setBackgroundColor(Color.parseColor("#00C853"))
                }
            }

            btnAcaoProduto.setOnClickListener {
                onProdutoClicked(produto)
            }
        }
    }

    override fun getItemCount(): Int = produtos.size

    fun atualizarLista(novaLista: List<Produto>) {
        this.produtos = novaLista
        notifyDataSetChanged()
    }
}
