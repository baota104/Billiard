package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemReceiptProductBinding
import com.example.billiard.domain.model.ReceiptItemUiModel

class ReceiptItemAdapter(
    private val onDeleteClick: (ReceiptItemUiModel) -> Unit
) : ListAdapter<ReceiptItemUiModel, ReceiptItemAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemReceiptProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReceiptItemUiModel) {
            binding.tvProductName.text = item.name

            // Format "24 x 8,500đ"
            val formattedPrice = "%,dđ".format(item.importPrice).replace(',', '.')
            binding.tvQuantityAndPrice.text = "${item.quantity} x $formattedPrice"

            // Format Tổng "204,000đ"
            binding.tvItemTotal.text = "%,dđ".format(item.totalPrice).replace(',', '.')

            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemReceiptProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<ReceiptItemUiModel>() {
        override fun areItemsTheSame(oldItem: ReceiptItemUiModel, newItem: ReceiptItemUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReceiptItemUiModel, newItem: ReceiptItemUiModel) = oldItem == newItem
    }
}