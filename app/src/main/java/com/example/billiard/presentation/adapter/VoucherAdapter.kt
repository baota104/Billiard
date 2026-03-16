package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemVoucherBinding
import com.example.billiard.domain.model.VoucherUiModel

class VoucherAdapter(
    private val onVoucherClick: (VoucherUiModel) -> Unit
) : ListAdapter<VoucherUiModel, VoucherAdapter.ViewHolder>(DiffCallback()) {

    private var selectedVoucherId: String? = null

    inner class ViewHolder(private val binding: ItemVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VoucherUiModel) {
            binding.tvVoucherCode.text = item.code
            binding.tvDiscountValue.text = item.discountText
            binding.tvMinOrder.text = item.minOrderText
            binding.tvExpiry.text = item.expiryText

            binding.badgeAI.visibility = if (item.isAiRecommended) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {

                if (selectedVoucherId == item.id) return@setOnClickListener

                val previousSelectedId = selectedVoucherId
                selectedVoucherId = item.id

                val oldPosition = currentList.indexOfFirst { it.id == previousSelectedId }
                val newPosition = currentList.indexOfFirst { it.id == selectedVoucherId }

                if (oldPosition != -1) notifyItemChanged(oldPosition)
                if (newPosition != -1) notifyItemChanged(newPosition)

                onVoucherClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<VoucherUiModel>() {
        override fun areItemsTheSame(oldItem: VoucherUiModel, newItem: VoucherUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VoucherUiModel, newItem: VoucherUiModel) = oldItem == newItem
    }
}