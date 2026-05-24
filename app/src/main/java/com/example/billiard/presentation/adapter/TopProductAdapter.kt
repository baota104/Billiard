package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemTopProductBinding

// Tạo một Data class tạm để chứa dữ liệu
data class TopProductItem(val name: String, val sold: Int, val revenue: Double)

class TopProductAdapter : RecyclerView.Adapter<TopProductAdapter.ViewHolder>() {

    private var items: List<TopProductItem> = emptyList()

    fun submitList(list: List<TopProductItem>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTopProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TopProductItem, position: Int) {
            binding.tvRank.text = (position + 1).toString()
            binding.tvProductName.text = item.name
            binding.tvQuantitySold.text = "${item.sold} đã bán"
            binding.tvRevenue.text = "%,.0fđ".format(item.revenue).replace(',', '.')
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTopProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}