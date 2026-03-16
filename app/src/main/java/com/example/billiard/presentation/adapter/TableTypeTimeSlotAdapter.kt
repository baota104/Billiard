package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemTimeSlotTableTypeBinding
import com.example.billiard.domain.model.TableTypeTimeSlotUiModel

class TableTypeTimeSlotAdapter(
    private val onItemClick: (TableTypeTimeSlotUiModel) -> Unit
) : ListAdapter<TableTypeTimeSlotUiModel, TableTypeTimeSlotAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTimeSlotTableTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TableTypeTimeSlotUiModel) {
            binding.tvTableName.text = item.name

            // Xử lý Thẻ Tag (Màu sắc & Text)
            binding.tvBadge.text = item.badgeText
            binding.cardBadge.setCardBackgroundColor(Color.parseColor(item.badgeColorHex))

            // Xử lý hiển thị cấu hình hiện tại
            val context = binding.root.context
            if (item.configCount > 0) {
                // Đã cấu hình
                binding.tvStatusText.text = "${item.configCount} khung giờ hoạt động"
                binding.tvStatusText.setTextColor(ContextCompat.getColor(context, R.color.text_title))

                // Set icon đồng hồ màu xanh
                binding.icStatus.setImageResource(R.drawable.ic_clock) // Đổi đúng tên icon của bạn
                binding.icStatus.setColorFilter(ContextCompat.getColor(context, R.color.blue))
            } else {
                // Chưa cấu hình
                binding.tvStatusText.text = "Chưa cấu hình"
                binding.tvStatusText.setTextColor(ContextCompat.getColor(context, R.color.text_gray))

                // Set icon vạch ngang màu xám
                binding.icStatus.setImageResource(android.R.drawable.ic_media_pause) // Dùng tạm icon pause hoặc icon trừ
                binding.icStatus.setColorFilter(ContextCompat.getColor(context, R.color.text_gray))
            }

             Glide.with(context).load(R.drawable.img_loaiban).into(binding.imgTable)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimeSlotTableTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<TableTypeTimeSlotUiModel>() {
        override fun areItemsTheSame(oldItem: TableTypeTimeSlotUiModel, newItem: TableTypeTimeSlotUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TableTypeTimeSlotUiModel, newItem: TableTypeTimeSlotUiModel) = oldItem == newItem
    }
}