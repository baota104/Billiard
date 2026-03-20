package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.databinding.ItemInventoryProductBinding
import com.example.billiard.domain.model.OrderServiceUiModel

class InventoryAdapter(
    private val onEditClick: (OrderServiceUiModel) -> Unit,
    private val onDeleteClick: (OrderServiceUiModel) -> Unit
) : ListAdapter<OrderServiceUiModel, InventoryAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemInventoryProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderServiceUiModel) {
            val context = binding.root.context

            // 1. Thông tin cơ bản
            binding.tvProductName.text = item.name

            // Mẹo: Dùng endTime làm Đơn vị (Vì OrderServiceUiModel không có trường Unit)
            // Nếu null thì để trống
            binding.tvUnit.text = item.endTime ?: ""

            // 2. Xử lý Badge Danh mục & Màu sắc
            binding.tvCategoryBadge.text = item.category.uppercase()
            when (item.category.lowercase()) {
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

            // 3. Xử lý Giá Nhập & Giá Bán
            // Mẹo: Model không có Giá Nhập, giả lập Giá Nhập = 70% Giá Bán
            val importPrice = (item.unitPrice * 0.7).toInt()
            binding.tvImportPrice.text = "%,dđ".format(importPrice).replace(',', '.')
            binding.tvSellPrice.text = "%,dđ".format(item.unitPrice).replace(',', '.')

            // 4. Xử lý Tồn kho (Dùng trường quantity)
            val currentStock = item.quantity
            binding.tvStockCount.text = currentStock.toString()

            // Tính toán Progress Bar (Giả định Max = 100 nếu không có)
            val maxStock = 100
            val progressPercent = (currentStock.toFloat() / maxStock * 100).toInt()
            binding.progressStock.progress = progressPercent.coerceIn(0, 100)

            // Đổi màu Progress Bar & Chữ trạng thái theo số lượng tồn
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

            // TODO: Bật lại dòng này nếu bạn dùng thư viện load ảnh (Glide/Coil)
            // Glide.with(context).load(item.imageUrl).into(binding.imgProduct)

            // 5. Sự kiện Nút bấm
            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<OrderServiceUiModel>() {
        override fun areItemsTheSame(oldItem: OrderServiceUiModel, newItem: OrderServiceUiModel) = oldItem.serviceId == newItem.serviceId
        override fun areContentsTheSame(oldItem: OrderServiceUiModel, newItem: OrderServiceUiModel) = oldItem == newItem
    }
}