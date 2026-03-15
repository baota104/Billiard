package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemOrderedServiceBinding
import com.example.billiard.domain.model.OrderServiceUiModel

class OrderServiceAdapter(

): ListAdapter<OrderServiceUiModel, OrderServiceAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemOrderedServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemOrderedServiceBinding):
        RecyclerView.ViewHolder(binding.root) {
            fun bind(item: OrderServiceUiModel) {
                with(binding) {
                    tvItemName.text = item.name
                    tvItemDesc.text = item.category
                    tvItemQty.text = item.displayQuantity
                    tvItemPrice.text = item.unitPrice.toString()

                }
            }
        }
    class DiffCallback : DiffUtil.ItemCallback<OrderServiceUiModel>(){
        override fun areItemsTheSame(
            oldItem: OrderServiceUiModel,
            newItem: OrderServiceUiModel
        ): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(
            oldItem: OrderServiceUiModel,
            newItem: OrderServiceUiModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}