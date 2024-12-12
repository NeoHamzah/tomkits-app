package com.neohamzah.tomkitsapp.ui.detailDisease

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neohamzah.tomkitsapp.data.remote.response.ProductList
import com.neohamzah.tomkitsapp.databinding.ItemProductBinding
import com.neohamzah.tomkitsapp.utils.normalizeUrl

class ProductAdapter(
    private val onItemClick: (ProductList) -> Unit = {}
) : ListAdapter<ProductList, ProductAdapter.ProductViewHolder>(DiffCallback()) {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: ProductList) {
            binding.textViewName.text = product.productName
            binding.textViewIngredients.text = "Active Ingredient: ${ product.activeIngredient }"
            Glide.with(binding.imgPhoto.context)
                .load(product.productImage)
                .into(binding.imgPhoto)

            binding.root.setOnClickListener {
                val productLink = normalizeUrl(product.productLink)
                if (!productLink.isNullOrBlank()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(productLink))
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(itemView.context, "Cannot open product link", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductList>() {
        override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
            return oldItem.productName == newItem.productName
        }

        override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
            return oldItem == newItem
        }
    }
}