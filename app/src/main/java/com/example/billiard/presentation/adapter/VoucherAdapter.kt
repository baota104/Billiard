package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemVoucherBinding
import com.example.billiard.domain.model.Voucher
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class VoucherAdapter(
    private val onVoucherClick: (Voucher) -> Unit
) : ListAdapter<Voucher, VoucherAdapter.ViewHolder>(DiffCallback()) {

    private var selectedVoucherId: Long? = null

    inner class ViewHolder(private val binding: ItemVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Voucher) {
            val formatter = DecimalFormat("#,###")

            binding.tvVoucherCode.text = item.code

            // Hiển thị Giảm giá tùy theo Loại Voucher
            // Nếu VoucherType là "PERCENTAGE" thì hiển thị "%", nếu là "AMOUNT" thì hiển thị "đ"
            if (item.voucherType.equals("PERCENTAGE", ignoreCase = true)) {
                binding.tvDiscountValue.text = "Giảm ${item.value.toInt()}%"
            } else {
                val formattedValue = formatter.format(item.value.toInt()).replace(',', '.')
                binding.tvDiscountValue.text = "Giảm $formattedValue đ"
            }

            // Đơn tối thiểu
            val formattedMinAmount = formatter.format(item.minimumAmount.toInt()).replace(',', '.')
            binding.tvMinOrder.text = "Đơn tối thiểu: $formattedMinAmount đ"

            // Format ngày hết hạn
            val endTimeStr = try {
                val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                sdfInput.timeZone = TimeZone.getTimeZone("UTC")
                val sdfOutput = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = sdfInput.parse(item.endTime)
                date?.let { sdfOutput.format(it) } ?: item.endTime
            } catch (e: Exception) {
                item.endTime
            }
            binding.tvExpiry.text = "Hết hạn: $endTimeStr"

            // Ẩn badge AI do Backend không có field phân biệt
            binding.badgeAI.visibility = View.GONE

            // Style chọn / không chọn
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

    class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher) = oldItem == newItem
    }
}