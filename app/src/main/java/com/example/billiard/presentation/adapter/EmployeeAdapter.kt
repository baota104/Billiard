package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.databinding.ItemEmployeeBinding
import com.example.billiard.domain.model.EmployeeUiModel

class EmployeeAdapter(
    private val onEditClick: (EmployeeUiModel) -> Unit,
    private val onDeleteClick: (EmployeeUiModel) -> Unit,
    private val onStatusChange: (EmployeeUiModel, Boolean) -> Unit
) : ListAdapter<EmployeeUiModel, EmployeeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmployeeUiModel) {
            binding.tvEmployeeName.text = item.name
            binding.tvUsername.text = item.username
            binding.tvRole.text = item.role
            binding.tvCreatedDate.text = "Ngày tạo: ${item.createdDate}"

            // Tạm thời ngắt Listener để set giá trị không bị trigger sai
            binding.switchActive.setOnCheckedChangeListener(null)
            binding.switchActive.isChecked = item.isActive

            // Xử lý Avatar: Có ảnh thì load ảnh, không có thì hiện chữ cái
            if (!item.avatarUrl.isNullOrEmpty()) {
                binding.imgAvatar.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                // TODO: Dùng Glide load ảnh vào binding.imgAvatar
            } else {
                binding.imgAvatar.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                binding.tvAvatarInitials.text = item.initials ?: item.name.take(1).uppercase()
            }

            // Gắn các sự kiện click
            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }

            binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
                item.isActive = isChecked
                onStatusChange(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<EmployeeUiModel>() {
        override fun areItemsTheSame(oldItem: EmployeeUiModel, newItem: EmployeeUiModel) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: EmployeeUiModel, newItem: EmployeeUiModel) = oldItem == newItem
    }
}