package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemReceiptProductBinding
import com.example.billiard.domain.model.PurchaseDetail

class ReceiptItemAdapter(
    private val onDeleteClick: (PurchaseDetail) -> Unit
) : ListAdapter<PurchaseDetail, ReceiptItemAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemReceiptProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PurchaseDetail) {
            val context = binding.root.context
            
            binding.tvProductName.text = item.productName

            val formattedPrice = "%,dđ".format(item.importPrice.toInt()).replace(',', '.')
            binding.tvQuantityAndPrice.text = "${item.quantity} x $formattedPrice"

            binding.tvItemTotal.text = "%,dđ".format(item.subTotal.toInt()).replace(',', '.')

            // Xử lý hiển thị ảnh
            if (item.imageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.img_ban) // Ảnh mặc định nếu rỗng
            }

            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemReceiptProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<PurchaseDetail>() {
        override fun areItemsTheSame(oldItem: PurchaseDetail, newItem: PurchaseDetail) = oldItem.productId == newItem.productId
        override fun areContentsTheSame(oldItem: PurchaseDetail, newItem: PurchaseDetail) = oldItem == newItem
    }
}