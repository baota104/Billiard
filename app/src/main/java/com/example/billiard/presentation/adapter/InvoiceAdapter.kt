package com.example.billiard.presentation.adapter
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemInvoiceBinding
import com.example.billiard.domain.model.Invoice // Nhớ import đúng model của bạn

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

        fun bind(invoice: Invoice) {
            binding.apply {
                tvInvoiceId.text = "#INV-${invoice.id}"

                // Giả định bạn có format ngày giờ. Tạm thời hiển thị startTime
                tvDateTime.text = invoice.startTime

                tvPaymentMethod.text = invoice.paymentMethod ?: "Tiền mặt"

                // Format tiền
                tvTotalAmount.text = "%,dđ".format(invoice.totalAmount.toLong()).replace(',', '.')

                // Xử lý màu sắc trạng thái
                tvStatus.text = invoice.status
                if (invoice.status == "PAID" || invoice.status == "Hoàn thành") {
                    tvStatus.text = "Hoàn thành"
                    cardStatus.setCardBackgroundColor(Color.parseColor("#10B981")) // Xanh lá
                    tvStatus.setTextColor(Color.WHITE)
                } else {
                    tvStatus.text = "Chưa thanh toán"
                    cardStatus.setCardBackgroundColor(Color.parseColor("#F3F4F6")) // Xám
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