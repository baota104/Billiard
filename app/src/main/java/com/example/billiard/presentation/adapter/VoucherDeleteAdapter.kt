package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemVoucherdeleteBinding
import com.example.billiard.domain.model.Voucher
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class VoucherDeleteAdapter(
    private val onVoucherClick: (Voucher) -> Unit,
    private val onDeleteClick: (Voucher) -> Unit // THÊM CALLBACK XÓA Ở ĐÂY
) : ListAdapter<Voucher, VoucherDeleteAdapter.ViewHolder>(DiffCallback()) {

    private var selectedVoucherId: Long? = null

    inner class ViewHolder(private val binding: ItemVoucherdeleteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Voucher) {
            val formatter = DecimalFormat("#,###")

            binding.tvVoucherCode.text = item.code

            if (item.voucherType.equals("PERCENTAGE", ignoreCase = true)) {
                binding.tvDiscountValue.text = "Giảm ${item.value.toInt()}%"
            } else {
                val formattedValue = formatter.format(item.value.toInt()).replace(',', '.')
                binding.tvDiscountValue.text = "Giảm $formattedValue đ"
            }

            val formattedMinAmount = formatter.format(item.minimumAmount.toInt()).replace(',', '.')
            binding.tvMinOrder.text = "Đơn tối thiểu: $formattedMinAmount đ"

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
            binding.badgeAI.visibility = View.GONE

            // SỰ KIỆN 1: CLick vào Icon Thùng rác
            binding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }

            // SỰ KIỆN 2: Click vào cả thẻ Item (Giữ nguyên logic của bạn)
//            binding.root.setOnClickListener {
//                if (selectedVoucherId == item.id) return@setOnClickListener
//
//                val previousSelectedId = selectedVoucherId
//                selectedVoucherId = item.id
//
//                val oldPosition = currentList.indexOfFirst { it.id == previousSelectedId }
//                val newPosition = currentList.indexOfFirst { it.id == selectedVoucherId }
//
//                if (oldPosition != -1) notifyItemChanged(oldPosition)
//                if (newPosition != -1) notifyItemChanged(newPosition)
//
//                onVoucherClick(item)
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVoucherdeleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(oldItem: Voucher, newItem: Voucher) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Voucher, newItem: Voucher) = oldItem == newItem
    }
}