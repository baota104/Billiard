package com.example.billiard.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemTableManagementBinding
import com.example.billiard.domain.model.DashboardTable

class BanListAdapter(
    private val onEditClick: (DashboardTable) -> Unit,
    private val onDeleteClick: (DashboardTable) -> Unit
) : ListAdapter<DashboardTable, BanListAdapter.TableViewHolder>(TableDiffCallback()) {

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

        fun bind(table: DashboardTable) {
            val context = binding.root.context

            with(binding) {
                tvTableName.text = table.name
                tvTableCategory.text = table.tableType
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white))

                // Xử lý load ảnh bằng Glide
                if (table.imageUrl.isNotEmpty()) {
                    Glide.with(context)
                        .load("https://res.cloudinary.com/dod4mribc/image/upload/v1775883715/uploads/yae6zwpdni3mp7lchuut.jpg")
                        .error(R.drawable.img_ban) // Fallback nếu URL chết/lỗi
                        .into(imgTable)
                } else {
                    // Fallback mặc định khi rỗng hoặc null
                    imgTable.setImageResource(R.drawable.img_ban)
                }

                when (table.status.uppercase()) {
                    "EMPTY", "AVAILABLE" -> {
                        tvStatus.text = "● Trống"
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.badge_empty))
                    }
                    "PLAYING", "RESERVED" -> {
                        tvStatus.text = "● Đang chơi"
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.card_red))
                    }
                    "MAINTENANCE", "MAINTAIN" -> {
                        tvStatus.text = "● Bảo trì"
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.help))
                    }
                    else -> {
                        tvStatus.text = "● Không rõ"
                        tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.empty_table))
                    }
                }

                btnEdit.setOnClickListener { onEditClick(table) }
                btnDelete.setOnClickListener { onDeleteClick(table) }
            }
        }
    }

    class TableDiffCallback : DiffUtil.ItemCallback<DashboardTable>() {
        override fun areItemsTheSame(oldItem: DashboardTable, newItem: DashboardTable) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DashboardTable, newItem: DashboardTable) = oldItem == newItem
    }
}