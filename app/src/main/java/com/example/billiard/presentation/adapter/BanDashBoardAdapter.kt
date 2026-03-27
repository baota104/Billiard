package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.databinding.ItemTableBinding
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableStatus

class BanDashBoardAdapter(
    private val onclick: (BanUiModel) -> Unit,
): ListAdapter<BanUiModel, BanDashBoardAdapter.BanViewHolder>(BanDiffCallback()){

    override fun onBindViewHolder(
        holder: BanViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BanViewHolder {
       val binding = ItemTableBinding.inflate(
           LayoutInflater.from(parent.context), parent, false
       )
       return BanViewHolder(binding)
    }


    inner class BanViewHolder(private val binding: ItemTableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ban: BanUiModel) {
            val context = binding.root.context // Lấy context để dùng cho ContextCompat

            with(binding) {
                tvTableName.text = ban.name
                tvTableType.text = ban.type

                // Màu chữ của Badge luôn là màu trắng ở mọi trạng thái
                tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.white))

                when (ban.status) {
                    TableStatus.PLAYING -> {
                        // 1. Badge (Nhãn góc phải)
                        tvStatusBadge.text = "● ĐANG CHƠI"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_red))

                        // 2. Text hành động dưới cùng
                        tvBottomAction.text = ban.timePlaying ?: "00:00:00"
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_playing_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_corner_graytran)
                    }

                    TableStatus.EMPTY -> {
                        tvStatusBadge.text = "● TRỐNG"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.table_empty_bg))

                        tvBottomAction.text = "Bàn trống"
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_empty_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_dashed_empty_table)
                    }

                    else -> {
                        // Bảo trì
                        tvStatusBadge.text = "● Bảo trì"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.table_maintenance_bg))

                        tvBottomAction.text = "Đang bảo trì"
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_maintenance_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_corner_graytran)
                    }
                }

                root.setOnClickListener { onclick(ban) }
            }
        }
    }
    class BanDiffCallback : DiffUtil.ItemCallback<BanUiModel>() {
        override fun areItemsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BanUiModel, newItem: BanUiModel): Boolean {
            return oldItem == newItem
        }
    }
}