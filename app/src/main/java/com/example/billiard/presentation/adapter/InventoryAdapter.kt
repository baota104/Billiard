package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemInventoryProductBinding
import com.example.billiard.domain.model.Product

class InventoryAdapter(
    private val onEditClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : ListAdapter<Product, InventoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemInventoryProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Product) {
            val context = binding.root.context

            binding.tvProductName.text = item.name

            // Hiện tại API ko có trường unit (Đơn vị), set tạm trống hoặc hiển thị id để debug
            binding.tvUnit.text = "ID: ${item.id}"

            binding.tvCategoryBadge.text = item.categoryName.uppercase()
            when (item.categoryName.lowercase()) {
                "đồ uống" -> {
                    binding.tvCategoryBadge.setTextColor(Color.parseColor("#1976D2"))
                    binding.cardCategoryBadge.setCardBackgroundColor(Color.parseColor("#E3F2FD"))
                }
                "đồ ăn" -> {
                    binding.tvCategoryBadge.setTextColor(Color.parseColor("#E65100"))
                    binding.cardCategoryBadge.setCardBackgroundColor(Color.parseColor("#FFF3E0"))
                }
                "thuốc lá", "dịch vụ" -> {
                    binding.tvCategoryBadge.setTextColor(Color.parseColor("#7B1FA2"))
                    binding.cardCategoryBadge.setCardBackgroundColor(Color.parseColor("#F3E5F5"))
                }
                else -> {
                    binding.tvCategoryBadge.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
                    binding.cardCategoryBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.divider_gray))
                }
            }

            // API ProductDto không có importPrice, giả lập bằng 70% giá bán để test UI
            val importPrice = (item.sellingPrice * 0.7).toInt()
            binding.tvImportPrice.text = "%,dđ".format(importPrice).replace(',', '.')
            binding.tvSellPrice.text = "%,dđ".format(item.sellingPrice.toInt()).replace(',', '.')

            val currentStock = item.stock
            binding.tvStockCount.text = currentStock.toString()

            // Giả định định mức tồn tối đa (Max stock)
            val maxStock = 100
            val progressPercent = (currentStock.toFloat() / maxStock * 100).toInt()
            binding.progressStock.progress = progressPercent.coerceIn(0, 100)

            when {
                currentStock > 20 -> {
                    binding.tvStockStatus.text = "Ổn định"
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.text_green))
                    binding.progressStock.setIndicatorColor(ContextCompat.getColor(context, R.color.primary_blue))
                }
                currentStock in 1..20 -> {
                    binding.tvStockStatus.text = "Sắp hết"
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.card_red))
                    binding.progressStock.setIndicatorColor(ContextCompat.getColor(context, R.color.card_red))
                }
                else -> {
                    binding.tvStockStatus.text = "Cần nhập"
                    binding.tvStockStatus.setTextColor(ContextCompat.getColor(context, R.color.help))
                    binding.progressStock.setIndicatorColor(ContextCompat.getColor(context, R.color.help))
                }
            }

            // Load ảnh
            if (item.imageUrl.isNotEmpty()) {
                Glide.with(context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.img_ban) // Ảnh lỗi/mặc định (có thể đổi)
            }

            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}