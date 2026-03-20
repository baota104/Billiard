package com.example.billiard.presentation.administrator.inventory.receipt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemSearchProductBinding
import com.example.billiard.domain.model.ServiceItemUiModel

class ProductSearchAdapter(
    private val onProductSelected: (ServiceItemUiModel) -> Unit
) : ListAdapter<ServiceItemUiModel, ProductSearchAdapter.ViewHolder>(DiffCallback()) {

    // Lưu lại ID của sản phẩm đang được chọn
    private var selectedProductId: String? = null

    inner class ViewHolder(private val binding: ItemSearchProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServiceItemUiModel) {
            binding.tvProductName.text = item.name
            binding.tvStock.text = "Kho: ${item.stock} cái"

            // Ẩn/Hiện dấu tick xanh tùy thuộc vào việc nó có đang được chọn hay không
            if (item.id == selectedProductId) {
                binding.icCheck.visibility = View.VISIBLE
            } else {
                binding.icCheck.visibility = View.INVISIBLE
            }

            // Xử lý sự kiện click
            binding.root.setOnClickListener {
                if (selectedProductId != item.id) {
                    val oldSelectedId = selectedProductId
                    selectedProductId = item.id

                    // Cập nhật lại UI cho 2 dòng bị ảnh hưởng (dòng cũ mất tick, dòng mới có tick)
                    val oldIndex = currentList.indexOfFirst { it.id == oldSelectedId }
                    val newIndex = currentList.indexOfFirst { it.id == selectedProductId }

                    if (oldIndex != -1) notifyItemChanged(oldIndex)
                    if (newIndex != -1) notifyItemChanged(newIndex)

                    // Bắn callback ra ngoài Fragment
                    onProductSelected(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<ServiceItemUiModel>() {
        override fun areItemsTheSame(oldItem: ServiceItemUiModel, newItem: ServiceItemUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ServiceItemUiModel, newItem: ServiceItemUiModel) = oldItem == newItem
    }
}