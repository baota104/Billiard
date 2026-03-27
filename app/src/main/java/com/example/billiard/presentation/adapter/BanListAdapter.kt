package com.example.billiard.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
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
            val context = binding.root.context

            // Dùng with(binding) để loại bỏ toàn bộ chữ "binding." dư thừa
            with(binding) {
                tvTableName.text = table.name
                tvTableCategory.text = table.type.uppercase()
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white))
                when (table.status) {
                    TableStatus.EMPTY -> {
                        tvStatus.text = "● Trống"
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_empty))
                    }
                    TableStatus.PLAYING -> {
                        tvStatus.text = "● Đang chơi"
               //         tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_playing_text))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.card_red))
                    }
                    TableStatus.MAINTAIN -> {
                        tvStatus.text = "● Bảo trì"
                //        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_maintain_text))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.help))
                    }
                    else -> {
                        tvStatus.text = "● Không rõ"
                //        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_empty_text))
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.empty_table))
                    }
                }

                // Tạm thời set màu nền cho ảnh trống
                imgTable.setBackgroundColor(ContextCompat.getColor(context, R.color.table_empty_bg))

                // Gắn sự kiện click cho 2 nút Edit và Delete
                btnEdit.setOnClickListener { onEditClick(table) }
                btnDelete.setOnClickListener { onDeleteClick(table) }
            }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<BanUiModel>() {
        override fun areItemsTheSame(oldItem: BanUiModel, newItem: BanUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BanUiModel, newItem: BanUiModel) = oldItem == newItem
    }
}