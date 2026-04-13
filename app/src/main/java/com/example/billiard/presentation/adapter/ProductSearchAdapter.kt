package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.databinding.ItemSearchProductBinding
import com.example.billiard.domain.model.Product

class ProductSearchAdapter(
    private val onProductSelected: (Product) -> Unit
) : ListAdapter<Product, ProductSearchAdapter.ViewHolder>(DiffCallback()) {

    // Lưu lại ID của sản phẩm đang được chọn
    private var selectedProductId: Int? = null

    inner class ViewHolder(private val binding: ItemSearchProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            val context = binding.root.context
            binding.tvProductName.text = item.name
            binding.tvStock.text = "Kho: ${item.stock} cái"

            if (item.id == selectedProductId) {
                binding.icCheck.visibility = View.VISIBLE
            } else {
                binding.icCheck.visibility = View.INVISIBLE
            }
            Glide.with(context)
                .load(item.imageUrl)
                .centerCrop()
                .into(binding.imgProduct)

            binding.root.setOnClickListener {
                if (selectedProductId != item.id) {
                    val oldSelectedId = selectedProductId
                    selectedProductId = item.id

                    val oldIndex = currentList.indexOfFirst { it.id == oldSelectedId }
                    val newIndex = currentList.indexOfFirst { it.id == selectedProductId }

                    if (oldIndex != -1) notifyItemChanged(oldIndex)
                    if (newIndex != -1) notifyItemChanged(newIndex)

                    onProductSelected(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}