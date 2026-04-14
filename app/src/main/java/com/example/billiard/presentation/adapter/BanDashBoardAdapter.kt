package com.example.billiard.presentation.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemTableBinding
import com.example.billiard.domain.model.DashboardTable
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class BanDashBoardAdapter(
    private val onclick: (DashboardTable) -> Unit
) : ListAdapter<DashboardTable, BanDashBoardAdapter.BanViewHolder>(BanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BanViewHolder {
        val binding = ItemTableBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BanViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: BanViewHolder) {
        super.onViewRecycled(holder)
        Log.d("BanDashboard_TIMER", "♻ onViewRecycled: Dừng timer cho ViewHolder tại vị trí ${holder.adapterPosition}")
        holder.stopTimer()
    }

    inner class BanViewHolder(private val binding: ItemTableBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val handler = Handler(Looper.getMainLooper())
        private var updateTimeRunnable: Runnable? = null

        // Track the current table ID to prevent a delayed runnable from updating a recycled view
        private var currentTableId: Int? = null

        fun bind(ban: DashboardTable) {
            val context = binding.root.context

            // 1. NGƯNG TIMER CŨ NGAY LẬP TỨC TRƯỚC KHI BIND DỮ LIỆU MỚI
            stopTimer()
            currentTableId = ban.id

            with(binding) {
                tvTableName.text = ban.name
                tvTableType.text = ban.tableType.ifBlank { "POOL" }.uppercase()
                tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.white))

                if (ban.imageUrl.isNotEmpty()) {
                    Glide.with(context)
                        .load(ban.imageUrl)
                        .error(R.drawable.img_ban)
                        .into(imgTable)
                } else {
                    imgTable.setImageResource(R.drawable.img_ban)
                }

                when (ban.status.uppercase()) {
                    "PLAYING", "RESERVED" -> {
                        tvStatusBadge.text = "● ĐANG CHƠI"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_red))
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_playing_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_corner_graytran)

                        if (ban.activeInvoice != null && !ban.activeInvoice.startAt.isNullOrEmpty()) {
                            Log.d("BanDashboard_TIMER", "▶ Khởi động timer cho Bàn: ${ban.name} | Bắt đầu lúc: ${ban.activeInvoice.startAt}")
                            startTimer(ban.activeInvoice.startAt, ban.id)
                        } else {
                            Log.e("BanDashboard_TIMER", " Lỗi: Bàn ${ban.name} Đang chơi nhưng activeInvoice hoặc startAt bị null!")
                            tvBottomAction.text = "Đang chơi (Lỗi giờ)"
                        }
                    }
                    "EMPTY", "AVAILABLE" -> {
                        tvStatusBadge.text = "● TRỐNG"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.table_empty_bg))
                        tvBottomAction.text = "Bàn trống"
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_empty_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_dashed_empty_table)
                    }
                    else -> {
                        tvStatusBadge.text = "● BẢO TRÌ"
                        cardBadge.setCardBackgroundColor(ContextCompat.getColor(context, R.color.table_maintenance_bg))
                        tvBottomAction.text = "Đang bảo trì"
                        tvBottomAction.setTextColor(ContextCompat.getColor(context, R.color.table_maintenance_text))
                        tvBottomAction.setBackgroundResource(R.drawable.bg_corner_graytran)
                    }
                }
                root.setOnClickListener { onclick(ban) }
            }
        }

        private fun startTimer(startAtISO: String, tableId: Int) {
            val startTimeInMillis = parseIsoDate(startAtISO)
            if (startTimeInMillis == 0L) {
                Log.e("BanDashboard_TIMER", " Lỗi Parse Date cho bàn ID $tableId. Chuỗi thời gian: $startAtISO")
                binding.tvBottomAction.text = "Lỗi đọc giờ"
                return
            }

            updateTimeRunnable = object : Runnable {
                override fun run() {
                    // Kẻ gác cổng: Nếu ViewHolder này đã bị recycle và gán cho bàn khác, THOÁT NGAY!
                    if (currentTableId != tableId) {
                        Log.w("BanDashboard_TIMER", " Đã chặn Runnable mồ côi chạy trên ViewHolder cũ (Bàn ID $tableId)")
                        return
                    }

                    val currentTime = System.currentTimeMillis()
                    val diffInMillis = currentTime - startTimeInMillis

                    if (diffInMillis > 0) {
                        val hours = (diffInMillis / (1000 * 60 * 60)) % 24
                        val minutes = (diffInMillis / (1000 * 60)) % 60
                        val seconds = (diffInMillis / 1000) % 60

                        val timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                        binding.tvBottomAction.text = timeString

                        // Log nhỏ giọt mỗi 10s để kiểm tra xem nó có đang chạy không
                        if (seconds % 10L == 0L) {
                            // Log.d("BanDashboard_TIMER", "⏳ Bàn $tableId đang chạy: $timeString")
                        }
                    } else {
                        binding.tvBottomAction.text = "00:00:00"
                    }

                    // Lặp lại sau 1 giây
                    handler.postDelayed(this, 1000)
                }
            }
            // Kích phát Runnable lần đầu tiên
            handler.post(updateTimeRunnable!!)
        }

        // Parse date logic is kept the same...
        private fun parseIsoDate(dateString: String): Long {
            try {
                val sdf1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                sdf1.timeZone = TimeZone.getTimeZone("UTC")
                return sdf1.parse(dateString)?.time ?: 0L
            } catch (e: Exception) {
                try {
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                    sdf2.timeZone = TimeZone.getTimeZone("UTC")
                    return sdf2.parse(dateString)?.time ?: 0L
                } catch (e2: Exception) {
                    try {
                        val sdf3 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        // Đã thay đổi: Loại bỏ UTC cho sdf3 để thử khớp với giờ Local nếu server không gửi múi giờ
                        // sdf3.timeZone = TimeZone.getTimeZone("UTC")
                        return sdf3.parse(dateString)?.time ?: 0L
                    } catch (e3: Exception) {
                        try {
                            val sdf4 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            return sdf4.parse(dateString)?.time ?: 0L
                        } catch (e4: Exception) {
                            return 0L
                        }
                    }
                }
            }
        }

        fun stopTimer() {
            updateTimeRunnable?.let {
                handler.removeCallbacks(it)
                updateTimeRunnable = null
            }
        }
    }

    // DiffCallback remains the same...
    class BanDiffCallback : DiffUtil.ItemCallback<DashboardTable>() {
        override fun areItemsTheSame(oldItem: DashboardTable, newItem: DashboardTable): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: DashboardTable, newItem: DashboardTable): Boolean {
            return oldItem == newItem
        }
    }
}