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
import com.example.billiard.databinding.ItemBankAccountBinding
import com.example.billiard.domain.model.Bank

class BankAdapter(
    private val onSetDefaultClick: (Bank) -> Unit,
    private val onDeleteClick: (Bank) -> Unit
) : ListAdapter<Bank, BankAdapter.BankViewHolder>(DiffCallback()) {

    inner class BankViewHolder(private val binding: ItemBankAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bank: Bank) {
            val context = binding.root.context

            // Đổ dữ liệu text
            binding.tvBankName.text = bank.bankName
            binding.tvAccountHolder.text = bank.bankAccountName.uppercase()
            binding.tvAccountNumber.text = formatAccountNumber(bank.bankAccountNo)

            if (bank.bankLogo.isNotEmpty()) {
                Glide.with(context)
                    .load(bank.bankLogo)
                    .centerCrop()
                    .into(binding.imgBankLogo)
            } else {
                binding.imgBankLogo.setImageResource(R.drawable.ic_bank) // Ảnh fallback
            }

            // Xử lý UI cho nút "Đặt mặc định" - Dựa theo trường bankStatus (true = Mặc định)
            if (bank.bankStatus) {
                binding.btnSetDefault.setCardBackgroundColor(ContextCompat.getColor(context, R.color.bg_icon_light))
                binding.btnSetDefault.strokeWidth = 0
                binding.icDefaultCheck.setImageResource(R.drawable.ic_check) 
                binding.icDefaultCheck.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_blue))
                binding.tvDefaultText.text = "Mặc định"
                binding.tvDefaultText.setTextColor(ContextCompat.getColor(context, R.color.primary_blue))
            } else {
                binding.btnSetDefault.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                binding.btnSetDefault.strokeWidth = (1f * context.resources.displayMetrics.density).toInt() // Viền 1dp
                binding.btnSetDefault.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_400)))

                binding.icDefaultCheck.setImageResource(R.drawable.ic_circle) 
                binding.icDefaultCheck.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.text_gray))
                binding.tvDefaultText.text = "Đặt mặc định"
                binding.tvDefaultText.setTextColor(ContextCompat.getColor(context, R.color.text_gray))
            }

            // Gắn sự kiện Click
            binding.btnSetDefault.setOnClickListener {
                if (!bank.bankStatus) {
                    onSetDefaultClick(bank)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(bank)
            }
        }
        
        // Cắt ẩn đi một phần số tài khoản: "8888 1234 5678" -> "8888 **** 5678"
        private fun formatAccountNumber(rawNumber: String): String {
            if (rawNumber.length < 8) return rawNumber
            val firstPart = rawNumber.substring(0, 4)
            val lastPart = rawNumber.substring(rawNumber.length - 4)
            return "$firstPart **** $lastPart"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding = ItemBankAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Bank>() {
        override fun areItemsTheSame(oldItem: Bank, newItem: Bank) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Bank, newItem: Bank) = oldItem == newItem
    }
}