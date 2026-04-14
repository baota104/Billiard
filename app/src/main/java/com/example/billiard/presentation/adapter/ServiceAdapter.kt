package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.databinding.ItemServiceGridBinding
import com.example.billiard.domain.model.Product

class ServiceAdapter(
    private val onItemClick: (Product) -> Unit
) : ListAdapter<Product, ServiceAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemServiceGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Product) {
            binding.tvServiceName.text = item.name

            // Format giá tiền trực tiếp từ số (VD: 20000 -> 20.000đ)
            binding.tvServicePrice.text = "%,dđ".format(item.sellingPrice.toLong()).replace(',', '.')

             Glide.with(binding.root.context).load(item.imageUrl)
                 .error(android.R.drawable.stat_notify_error)
                 .into(binding.imgService)

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}