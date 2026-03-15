package com.example.billiard.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemTableManagementBinding
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableStatus

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
            // Đã đổi table.ten -> table.name, table.theloai -> table.type để khớp model
            binding.tvTableName.text = table.name
            binding.tvTableCategory.text = table.type.uppercase()

            // Dùng trực tiếp Enum TableStatus thay vì check chuỗi String
            when (table.status) {
                TableStatus.EMPTY -> {
                    binding.tvStatus.text = "● Trống"
                    binding.tvStatus.setTextColor(Color.parseColor("#78829D"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F2F5"))
                }
                TableStatus.PLAYING -> {
                    binding.tvStatus.text = "● Đang chơi"
                    binding.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
                }
                TableStatus.MAINTAIN -> {
                    binding.tvStatus.text = "● Bảo trì"
                    binding.tvStatus.setTextColor(Color.parseColor("#FF9800"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FFF3E0"))
                }
                else -> {
                    binding.tvStatus.text = "● Không rõ"
                    binding.tvStatus.setTextColor(Color.parseColor("#78829D"))
                    binding.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F0F2F5"))
                }
            }

            // Tạm thời set màu nền cho ảnh trống
            binding.imgTable.setBackgroundColor(Color.parseColor("#2E7D32"))

            // Gắn sự kiện click cho 2 nút Edit và Delete
            binding.btnEdit.setOnClickListener { onEditClick(table) }
            binding.btnDelete.setOnClickListener { onDeleteClick(table) }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<BanUiModel>() {
        override fun areItemsTheSame(oldItem: BanUiModel, newItem: BanUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BanUiModel, newItem: BanUiModel) = oldItem == newItem
    }
}