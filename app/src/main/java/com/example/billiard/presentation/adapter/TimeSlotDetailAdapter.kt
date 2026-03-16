package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemTimeSlotDetailBinding
import com.example.billiard.domain.model.SlotThemeType
import com.example.billiard.domain.model.TimeSlotDetailUiModel

class TimeSlotDetailAdapter(
    private val onEditClick: (TimeSlotDetailUiModel) -> Unit,
    private val onDeleteClick: (TimeSlotDetailUiModel) -> Unit
) : ListAdapter<TimeSlotDetailUiModel, TimeSlotDetailAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTimeSlotDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TimeSlotDetailUiModel) {
            binding.tvSlotTitle.text = item.title
            binding.tvSlotDesc.text = item.description
            binding.tvTimeRange.text = item.timeRange
            binding.tvPrice.text = item.formattedPrice

            // Xử lý bo tròn màu nền cho Icon
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(item.slotType.bgHex))
            }
            binding.iconContainer.background = bgDrawable
            binding.imgSlotIcon.setColorFilter(Color.parseColor(item.slotType.iconTintHex))

            // TODO: (Tùy chọn) Gán icon khác nhau tùy theo loại
            // VD: if(item.slotType == MORNING) imgSlotIcon.setImageResource(R.drawable.ic_sun) ...

            // Nếu là cuối tuần thì đổi icon đồng hồ thành icon Lịch
            if (item.slotType == SlotThemeType.WEEKEND) {
                binding.icTimeInfo.setImageResource(android.R.drawable.ic_menu_month)
            } else {
                binding.icTimeInfo.setImageResource(android.R.drawable.ic_menu_recent_history)
            }

            // Gắn sự kiện click
            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimeSlotDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<TimeSlotDetailUiModel>() {
        override fun areItemsTheSame(oldItem: TimeSlotDetailUiModel, newItem: TimeSlotDetailUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TimeSlotDetailUiModel, newItem: TimeSlotDetailUiModel) = oldItem == newItem
    }
}