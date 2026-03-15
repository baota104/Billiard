package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat // Thêm import này
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R // Thêm import R của dự án bạn
import com.example.billiard.databinding.ItemTransferTableBinding
import com.example.billiard.domain.model.BanUiModel
import com.google.android.material.card.MaterialCardView

class TransferTableAdapter(
    private val onTableSelected: (BanUiModel?) -> Unit
) : ListAdapter<BanUiModel, TransferTableAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransferTableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position) // Truyền thêm position vào đây
    }

    inner class ViewHolder(private val binding: ItemTransferTableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BanUiModel, position: Int) {
            val isSelected = position == selectedPosition
            val context = binding.root.context // Lấy context để gọi màu

            with(binding) {
                tvTableName.text = item.name
                tvTableType.text = item.type

                if (isSelected) {
                    (root as MaterialCardView).strokeColor = ContextCompat.getColor(context, R.color.blue)
                    viewTopBg.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_date))
                    imgTableIcon.setColorFilter(ContextCompat.getColor(context, R.color.blue))

                    tvTableName.setTextColor(ContextCompat.getColor(context, R.color.blue))
                    tvTableType.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))

                    cardBadge.visibility = View.GONE
                    imgCheckmark.visibility = View.VISIBLE
                    cardBadge.setCardBackgroundColor(Color.TRANSPARENT) // Cái này mặc định hệ thống nên giữ nguyên

                } else {

                    (root as MaterialCardView).strokeColor = ContextCompat.getColor(context, R.color.divider_gray)
                    viewTopBg.setBackgroundColor(ContextCompat.getColor(context, R.color.different_white))
                    imgTableIcon.setColorFilter(ContextCompat.getColor(context, R.color.gray_2))

                    tvTableName.setTextColor(ContextCompat.getColor(context, R.color.text_title))
                    tvTableType.setTextColor(ContextCompat.getColor(context, R.color.text_gray))


                    cardBadge.visibility = View.VISIBLE
                    imgCheckmark.visibility = View.GONE
                    cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.badge))
                }

                root.setOnClickListener {
                    val previousSelected = selectedPosition
                    selectedPosition = adapterPosition

                    notifyItemChanged(previousSelected)
                    notifyItemChanged(selectedPosition)

                    onTableSelected(item)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BanUiModel>() {
        override fun areItemsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem == newItem
        }
    }
}