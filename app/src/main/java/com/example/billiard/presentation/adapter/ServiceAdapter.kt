package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemServiceGridBinding
import com.example.billiard.domain.model.ServiceItemUiModel

class ServiceAdapter(
    private val onItemClick: (ServiceItemUiModel) -> Unit
) : ListAdapter<ServiceItemUiModel, ServiceAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemServiceGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ServiceItemUiModel) {
            binding.tvServiceName.text = item.name

            // Lấy giá tiền đã được format sẵn từ UiModel (VD: 20.000đ)
            binding.tvServicePrice.text = item.formattedPrice

            // Glide.with(binding.root.context).load(item.imageUrl).into(binding.imgService)

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

    class DiffCallback : DiffUtil.ItemCallback<ServiceItemUiModel>() {
        override fun areItemsTheSame(oldItem: ServiceItemUiModel, newItem: ServiceItemUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ServiceItemUiModel, newItem: ServiceItemUiModel) = oldItem == newItem
    }
}