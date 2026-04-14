package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.gifdecoder.GifHeader
import com.example.billiard.R
import com.example.billiard.databinding.ItemOrderedServiceBinding
import com.example.billiard.domain.model.OrderDetail
import com.example.billiard.domain.model.OrderServiceUiModel

class OrderDetailAdapter(

): ListAdapter<OrderDetail, OrderDetailAdapter.ViewHolder>(DiffCallback()) {

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

                // Load ảnh bằng Glide, thêm error/placeholder để tránh crash nếu link ảnh hỏng
                Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // Thay bằng icon loading của bạn
                    .error(R.drawable.ic_launcher_background) // Thay bằng icon lỗi của bạn
                    .into(imgItem)

                tvItemName.text = item.productName
                // Nếu OrderDetail không có category, bạn có thể truyền tên danh mục mặc định hoặc ẩn đi
                tvItemDesc.text = "Sản phẩm"

                tvItemQty.text = "x${item.quantity}"

                // Format giá tiền (Ví dụ: 30000 -> 30.000đ)
                tvItemPrice.text = "%,dđ".format(item.price.toInt()).replace(',', '.')
            }
        }
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