package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemImportHistoryBinding
import com.example.billiard.domain.model.PurchaseHistory
import java.text.SimpleDateFormat
import java.util.Locale

class ImportHistoryAdapter(
    private val onItemClick: (PurchaseHistory) -> Unit
) : ListAdapter<PurchaseHistory, ImportHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemImportHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PurchaseHistory) {
            // Đổ dữ liệu vào UI
            binding.tvReceiptId.text = "PN${String.format("%03d", item.purchaseId)}"

            // Xử lý convert chuỗi ngày từ Backend (Ví dụ: "2026-04-12T14:52:09.722Z" thành "12/04/2026 14:52")
            binding.tvImportDate.text = try {
                val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val sdfOutput = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = sdfInput.parse(item.purchaseDate)
                date?.let { sdfOutput.format(it) } ?: item.purchaseDate
            } catch (e: Exception) {
                item.purchaseDate
            }

            // Định dạng tiền
            binding.tvTotalPrice.text = "%,dđ".format(item.totalPrice.toInt()).replace(',', '.')
            binding.tvEmployeeName.text = "Nhân viên: ${item.employeeName}"

            // Sự kiện bấm vào 1 thẻ hóa đơn (để xem chi tiết phiếu nhập)
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImportHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<PurchaseHistory>() {
        override fun areItemsTheSame(oldItem: PurchaseHistory, newItem: PurchaseHistory): Boolean {
            return oldItem.purchaseId == newItem.purchaseId
        }

        override fun areContentsTheSame(oldItem: PurchaseHistory, newItem: PurchaseHistory): Boolean {
            return oldItem == newItem
        }
    }
}