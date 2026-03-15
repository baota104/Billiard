package com.example.billiard.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
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

            with(binding) {
                tvTableName.text = ban.name
                tvTableType.text = ban.type

                when (ban.status) {
                    TableStatus.PLAYING -> {
                        // 1. Badge (Nhãn góc phải)
                        tvStatusBadge.text = "● ĐANG CHƠI"
                        tvStatusBadge.setTextColor(Color.parseColor("#FFFFFF"))
                        cardBadge.setCardBackgroundColor(Color.parseColor("#F44336"))

                        // 2. Text hành động dưới cùng
                        tvBottomAction.text = ban.timePlaying ?: "00:00:00"
                        tvBottomAction.setTextColor(Color.parseColor("#1A237E"))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_rounded_gray_outline)
                    }

                    TableStatus.EMPTY -> {
                        tvStatusBadge.text = "● TRỐNG"
                        tvStatusBadge.setTextColor(Color.parseColor("#FFFFFF"))
                        cardBadge.setCardBackgroundColor(Color.parseColor("#8822C55E"))

                        tvBottomAction.text = "Bàn trống"
                        tvBottomAction.setTextColor(Color.parseColor("#9AA6B8"))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_dashed_empty_table)
                    }

                    else -> {
                        tvStatusBadge.text = "● Bảo trì"
                        tvStatusBadge.setTextColor(Color.parseColor("#FFFFFF"))
                        cardBadge.setCardBackgroundColor(Color.parseColor("#88FF9800"))
                        tvBottomAction.text = "Đang bảo trì"
                        tvBottomAction.setTextColor(Color.parseColor("#FF9800"))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_rounded_gray_outline)
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