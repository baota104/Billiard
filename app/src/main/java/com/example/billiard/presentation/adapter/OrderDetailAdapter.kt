package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemOrderedServiceBinding
import com.example.billiard.domain.model.OrderDetail

class OrderDetailAdapter : ListAdapter<OrderDetail, OrderDetailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderedServiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemOrderedServiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: OrderDetail) {
            with(binding) {
                val context = root.context

                // Load ảnh bằng Glide
                Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imgItem)

                tvItemName.text = item.productName

                // Hiển thị số lượng
                tvItemQty.text = "x${item.quantity}"

                // Format giá tiền
                val formattedPrice = "%,dđ".format(item.price.toInt()).replace(',', '.')

                // KỂM TRA LOẠI SẢN PHẨM (Sử dụng ignoreCase để tránh lỗi viết hoa/thường)
                if (item.categorytype.equals("RETAIL", ignoreCase = true)) {
                    tvItemDesc.text = "Đồ ăn / Thức uống"
                    tvItemPrice.text = formattedPrice
                } else {
                    // Mặc định hoặc RENTAL
                    tvItemDesc.text = "Dịch vụ thuê"
                    tvItemPrice.text = "$formattedPrice/h"
                }
            }
        }
    }

    // Helper method để lấy item bên ngoài (dành cho tính năng vuốt xóa)
    fun getOrderDetailAt(position: Int): OrderDetail {
        return getItem(position)
    }

    class DiffCallback : DiffUtil.ItemCallback<OrderDetail>() {
        override fun areItemsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem == newItem
        }
    }
}