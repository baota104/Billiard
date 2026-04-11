package com.example.billiard.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.databinding.ItemEmployeeBinding
import com.example.billiard.domain.model.Employee

class EmployeeAdapter(
    private val onEditClick: (Employee) -> Unit,
    private val onDeleteClick: (Employee) -> Unit,
    private val onStatusChange: (Employee, Boolean) -> Unit
) : ListAdapter<Employee, EmployeeAdapter.ViewHolder>(DiffCallback()) {

    inner class ViewHolder(private val binding: ItemEmployeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Employee) {
            val context = binding.root.context
            
            binding.tvEmployeeName.text = item.fullName
            // API không có trường username, dùng email thay thế hiển thị phụ
            binding.tvUsername.text = item.email.ifBlank { "Không có email" }
            binding.tvRole.text = item.role

            binding.switchActive.setOnCheckedChangeListener(null)
            binding.switchActive.isChecked = item.isActive

            // Xử lý Avatar: Có ảnh thì load ảnh, không có thì hiện chữ cái
            if (item.imageUrl.isNotEmpty()) {
                binding.imgAvatar.visibility = View.VISIBLE
                binding.tvAvatarInitials.visibility = View.GONE
                Glide.with(context)
                    .load(item.imageUrl)
                    .centerCrop()
                    .into(binding.imgAvatar)
            } else {
                binding.imgAvatar.visibility = View.GONE
                binding.tvAvatarInitials.visibility = View.VISIBLE
                
                // Lấy chữ cái đầu tiên của Tên để làm Avatar giả
                val initial = if (item.firstName.isNotBlank()) item.firstName.take(1) else "?"
                binding.tvAvatarInitials.text = initial.uppercase()
            }

            // Gắn các sự kiện click
            binding.btnEdit.setOnClickListener { onEditClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }

            binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
                onStatusChange(item, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEmployeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    class DiffCallback : DiffUtil.ItemCallback<Employee>() {
        override fun areItemsTheSame(oldItem: Employee, newItem: Employee) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Employee, newItem: Employee) = oldItem == newItem
    }
}