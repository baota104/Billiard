package com.example.billiard.presentation.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.databinding.ItemBankAccountBinding
import com.example.billiard.domain.model.BankUiModel

class BankAdapter(
    private val onSetDefaultClick: (BankUiModel) -> Unit,
    private val onDeleteClick: (BankUiModel) -> Unit
) : ListAdapter<BankUiModel, BankAdapter.BankViewHolder>(DiffCallback()) {

    inner class BankViewHolder(private val binding: ItemBankAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bank: BankUiModel) {
            val context = binding.root.context

            // Đổ dữ liệu text
            binding.tvBankName.text = bank.bankName
            binding.tvAccountHolder.text = bank.accountHolder.uppercase()
            binding.tvAccountNumber.text = bank.formattedAccountNumber

            // Glide.with(context).load(bank.logoUrl).into(binding.imgBankLogo)

            // Xử lý UI cho nút "Đặt mặc định"
            if (bank.isDefault) {
                // ĐÃ LÀ MẶC ĐỊNH -> Đổi sang màu xanh nhạt, bỏ viền
                binding.btnSetDefault.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bg_icon_light))
                binding.btnSetDefault.strokeWidth = 0
                binding.icDefaultCheck.setImageResource(R.drawable.ic_check) // Nhớ thêm icon tick tròn vào res/drawable
                binding.icDefaultCheck.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_blue))
                binding.tvDefaultText.text = "Mặc định"
                binding.tvDefaultText.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            } else {
                // BÌNH THƯỜNG -> Nền trắng, có viền xám
                binding.btnSetDefault.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                binding.btnSetDefault.strokeWidth = (1f * context.resources.displayMetrics.density).toInt() // Viền 1dp
                binding.btnSetDefault.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_400)))

                binding.icDefaultCheck.setImageResource(R.drawable.ic_circle) // Nhớ thêm icon vòng tròn rỗng vào res/drawable
                binding.icDefaultCheck.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_gray))
                binding.tvDefaultText.text = "Đặt mặc định"
                binding.tvDefaultText.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            }

            // Gắn sự kiện Click
            binding.btnSetDefault.setOnClickListener {
                if (!bank.isDefault) {
                    onSetDefaultClick(bank)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(bank)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding = ItemBankAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<BankUiModel>() {
        override fun areItemsTheSame(oldItem: BankUiModel, newItem: BankUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BankUiModel, newItem: BankUiModel) = oldItem == newItem
    }
}