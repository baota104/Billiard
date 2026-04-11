package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemTimeSlotDetailBinding
import com.example.billiard.domain.model.PriceList
import java.text.DecimalFormat

class TimeSlotDetailAdapter(
    private val onEditClick: (PriceList) -> Unit,
    private val onDeleteClick: (PriceList) -> Unit
) : ListAdapter<PriceList, TimeSlotDetailAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemTimeSlotDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PriceList) {
            // Bảo vệ chuỗi, chống lỗi StringIndexOutOfBoundsException
            // Khi backend trả về null hoặc chuỗi rỗng thì cho hiển thị mặc định
            val startTimeSafe = if (item.startTime.length >= 5) item.startTime else "00:00:00"
            val endTimeSafe = if (item.endTime.length >= 5) item.endTime else "00:00:00"

            val startShort = startTimeSafe.take(5)
            val endShort = endTimeSafe.take(5)

            // Lấy giờ đầu tiên để phán đoán CA (Ca sáng/Chiều/Tối) để giả lập title
            // Dùng try-catch kết hợp if-else để đảm bảo lấy được giờ chuẩn
            val startHourString = if (startShort.length >= 2) startShort.substring(0, 2) else "12"
            val startHour = startHourString.toIntOrNull() ?: 12
            val title = if (startHour < 12) "Ca Sáng" else if (startHour < 18) "Ca Chiều" else "Ca Tối"

            // Màu sắc theme
            val bgHex: String
            val iconTintHex: String
            when (startHour) {
                in 6..11 -> { // Sáng (Vàng)
                    bgHex = "#FFF8E1"
                    iconTintHex = "#FBC02D"
                }
                in 12..17 -> { // Chiều (Cam)
                    bgHex = "#E3F2FD"
                    iconTintHex = "#F57C00"
                }
                else -> { // Tối/Đêm (Xanh)
                    bgHex = "#EDE7F6"
                    iconTintHex = "#1976D2"
                }
            }

            // Gán dữ liệu lên UI
            binding.tvSlotTitle.text = "$title ($startShort)"
            binding.tvSlotDesc.text = "Khung giờ ${item.tableType}"
            binding.tvTimeRange.text = "$startShort - $endShort"

            val formatter = DecimalFormat("#,###")
            binding.tvPrice.text = "${formatter.format(item.unitPrice)} đ"

            // Xử lý bo tròn màu nền cho Icon
            val bgDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(bgHex))
            }
            binding.iconContainer.background = bgDrawable
            binding.imgSlotIcon.setColorFilter(Color.parseColor(iconTintHex))
            binding.icTimeInfo.setImageResource(android.R.drawable.ic_menu_recent_history)

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

    class DiffCallback : DiffUtil.ItemCallback<PriceList>() {
        override fun areItemsTheSame(oldItem: PriceList, newItem: PriceList) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PriceList, newItem: PriceList) = oldItem == newItem
    }
}