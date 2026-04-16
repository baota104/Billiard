package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemInvoiceBinding
import com.example.billiard.domain.model.Invoice
import java.text.SimpleDateFormat
import java.util.Locale

class InvoiceAdapter(
    private val onItemClick: (Invoice) -> Unit
) : ListAdapter<Invoice, InvoiceAdapter.InvoiceViewHolder>(InvoiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = ItemInvoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InvoiceViewHolder(private val binding: ItemInvoiceBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                // ✅ ĐÃ SỬA: Dùng adapterPosition cho các bản RecyclerView cũ
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(invoice: Invoice) {
            binding.apply {
                tvInvoiceId.text = "#HD${invoice.id}"

                // Format thời gian thành: "25/02/2026 • 14:30"
                val dateStr = try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())
                    val parsedDate = inputFormat.parse(invoice.startTime ?: "")
                    parsedDate?.let { outputFormat.format(it) } ?: invoice.startTime
                } catch (e: Exception) {
                    invoice.startTime // Fallback nếu parse lỗi
                }
                tvDateTime.text = dateStr

                tvPaymentMethod.text = invoice.paymentMethod ?: "Tiền mặt"

                // Format tiền tệ
                tvTotalAmount.text = "%,.0fđ".format(invoice.totalAmount).replace(',', '.')

                // Xử lý màu sắc trạng thái
                if (invoice.status == "PAID" || invoice.status == "Hoàn thành") {
                    tvStatus.text = "Hoàn thành"
                    cardStatus.setCardBackgroundColor(Color.parseColor("#10B981")) // Màu xanh lá
                    tvStatus.setTextColor(Color.WHITE)
                } else {
                    tvStatus.text = "Chưa thanh toán"
                    cardStatus.setCardBackgroundColor(Color.parseColor("#F3F4F6")) // Màu xám
                    tvStatus.setTextColor(Color.parseColor("#4B5563"))
                }
            }
        }
    }

    class InvoiceDiffCallback : DiffUtil.ItemCallback<Invoice>() {
        override fun areItemsTheSame(oldItem: Invoice, newItem: Invoice): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Invoice, newItem: Invoice): Boolean = oldItem == newItem
    }
}