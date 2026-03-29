package com.example.billiard.presentation.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemImportHistoryBinding
import com.example.billiard.domain.model.ImportHistoryUiModel

class ImportHistoryAdapter(
    private val onItemClick: (ImportHistoryUiModel) -> Unit
) : ListAdapter<ImportHistoryUiModel, ImportHistoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemImportHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ImportHistoryUiModel) {
            // Đổ dữ liệu vào UI
            binding.tvReceiptId.text = item.receiptCode
            binding.tvImportDate.text = item.importDate
            binding.tvTotalPrice.text = item.formattedTotalAmount
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

    class DiffCallback : DiffUtil.ItemCallback<ImportHistoryUiModel>() {
        override fun areItemsTheSame(oldItem: ImportHistoryUiModel, newItem: ImportHistoryUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImportHistoryUiModel, newItem: ImportHistoryUiModel): Boolean {
            return oldItem == newItem
        }
    }
}