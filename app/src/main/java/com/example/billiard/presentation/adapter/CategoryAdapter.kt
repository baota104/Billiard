package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.databinding.ItemCategoryChipBinding
import com.example.billiard.domain.model.CategoryUiModel
import com.google.android.material.card.MaterialCardView

class CategoryAdapter(
    private val onCategoryClick: (CategoryUiModel) -> Unit
) : ListAdapter<CategoryUiModel, CategoryAdapter.ViewHolder>(DiffCallback()) {

    // Mặc định chọn vị trí số 0 (Nút "Tất cả")
    private var selectedPosition = 0

    inner class ViewHolder(private val binding: ItemCategoryChipBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryUiModel, position: Int) {
            binding.tvCategoryName.text = item.name
            val context = binding.root.context
            val isSelected = position == selectedPosition

            if (isSelected) {
                // Trạng thái ĐÃ CHỌN: Nền xanh, chữ trắng, viền xanh
                (binding.root as MaterialCardView).apply {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    strokeColor = ContextCompat.getColor(context, R.color.blue)
                }
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                // Trạng thái BÌNH THƯỜNG: Nền trắng, chữ xám, viền xám
                (binding.root as MaterialCardView).apply {
                    setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    strokeColor = ContextCompat.getColor(context, R.color.divider_gray)
                }
                binding.tvCategoryName.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            }

            // Xử lý sự kiện click
            binding.root.setOnClickListener {
                if (selectedPosition == adapterPosition) return@setOnClickListener // Bấm lại cái cũ thì bỏ qua

                val previousSelected = selectedPosition
                selectedPosition = adapterPosition

                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)

                onCategoryClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class DiffCallback : DiffUtil.ItemCallback<CategoryUiModel>() {
        override fun areItemsTheSame(oldItem: CategoryUiModel, newItem: CategoryUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CategoryUiModel, newItem: CategoryUiModel) = oldItem == newItem
    }
}