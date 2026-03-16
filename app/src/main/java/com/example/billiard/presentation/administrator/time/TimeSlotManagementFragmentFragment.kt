package com.example.billiard.presentation.administrator.time

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentTimeSlotManagementFragmentBinding
import com.example.billiard.domain.model.TableTypeTimeSlotUiModel
import com.example.billiard.presentation.adapter.TableTypeTimeSlotAdapter


class TimeSlotManagementFragmentFragment : BaseFragment<FragmentTimeSlotManagementFragmentBinding>(FragmentTimeSlotManagementFragmentBinding::inflate) {

    private lateinit var timeSlotAdapter: TableTypeTimeSlotAdapter

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Tạo thiết lập mới", Toast.LENGTH_SHORT).show()
        }

        timeSlotAdapter = TableTypeTimeSlotAdapter { selectedItem ->
            Toast.makeText(requireContext(), "Thiết lập giá cho: ${selectedItem.name}", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_timeSlotManagementFragmentFragment_to_timeSlotDetailFragment)
        }

        binding.rvTimeSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timeSlotAdapter
        }

        createMockData()
    }

    private fun createMockData() {
        val mockData = listOf(
            TableTypeTimeSlotUiModel("1", "Bàn Lỗ (Pool)", "Phổ biến", "#2962FF", "", 3),
            TableTypeTimeSlotUiModel("2", "Bàn Phăng (Carom)", "Pháp", "#9E9E9E", "", 2),
            TableTypeTimeSlotUiModel("3", "Bàn VIP", "☆ VIP", "#FFB300", "", 1),
            TableTypeTimeSlotUiModel("4", "Bàn Snooker", "Quốc tế", "#4CAF50", "", 0) // 0 = Chưa cấu hình
        )
        timeSlotAdapter.submitList(mockData)
    }

    override fun observeData() {}
}