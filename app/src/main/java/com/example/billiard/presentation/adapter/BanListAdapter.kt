package com.example.billiard.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemTableManagementBinding
import com.example.billiard.domain.model.Ban
import com.example.billiard.presentation.model.BanUiModel

class BanListAdapter(
    private val onEditClick: (BanUiModel) -> Unit,
    private val onDeleteClick: (BanUiModel) -> Unit
) : ListAdapter<BanUiModel, BanListAdapter.TableViewHolder>(TableDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = ItemTableManagementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TableViewHolder(private val binding: ItemTableManagementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(table: BanUiModel) {
            binding.tvTableName.text = table.ten
            binding.tvTableCategory.text = table.theloai.uppercase()

            val currentStatus = table.trangthai.lowercase().trim()
            when {
                currentStatus.contains("trống") || currentStatus.contains("trong") -> {
                    binding.tvStatus.text = "● Trống"
                    binding.tvStatus.setTextColor(Color.parseColor("#78829D"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F2F5"))
                }
                currentStatus.contains("đang chơi") || currentStatus.contains("dang choi") -> {
                    binding.tvStatus.text = "● Đang chơi"
                    binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
                }
                currentStatus.contains("đã đặt") || currentStatus.contains("da dat") -> {
                    binding.tvStatus.text = "📅 Đã đặt"
                    binding.tvStatus.setTextColor(Color.parseColor("#5C6BC0"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8EAF6"))
                }
                currentStatus.contains("bảo trì") || currentStatus.contains("bao tri") -> {
                    binding.tvStatus.text = "🔧 Bảo trì"
                    binding.tvStatus.setTextColor(Color.parseColor("#FF9800"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
                }
                else -> {
                    binding.tvStatus.text = "● Không rõ"
                    binding.tvStatus.setTextColor(Color.parseColor("#78829D"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F2F5"))
                }
            }

            if (table.imageUrl != null) {
                // TODO: Dùng thư viện Glide load ảnh. Tạm thời để trống.
                // Glide.with(binding.root).load(table.imageUrl).into(binding.imgTable)
            } else {
                // Tạm thời nếu không có ảnh thì set màu nền mặc định cho giống thiết kế
                binding.imgTable.setBackgroundColor(Color.parseColor("#2E7D32"))
            }

            binding.btnEdit.setOnClickListener { onEditClick(table) }
            binding.btnDelete.setOnClickListener { onDeleteClick(table) }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<BanUiModel>() {
        override fun areItemsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem == newItem
        }
    }
}