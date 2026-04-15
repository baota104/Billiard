package com.example.billiard.presentation.administrator.time

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentTimeSlotManagementFragmentBinding
import com.example.billiard.domain.model.TableTypeTimeSlotUiModel
import com.example.billiard.presentation.adapter.TableTypeTimeSlotAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimeSlotManagementFragmentFragment : BaseFragment<FragmentTimeSlotManagementFragmentBinding>(FragmentTimeSlotManagementFragmentBinding::inflate) {

    private val viewModel: PriceListViewModel by viewModels()
    private lateinit var timeSlotAdapter: TableTypeTimeSlotAdapter

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAdd.setOnClickListener {
            Toast.makeText(requireContext(), "Chưa hỗ trợ thêm loại bàn mới", Toast.LENGTH_SHORT).show()
        }

        timeSlotAdapter = TableTypeTimeSlotAdapter { selectedItem ->
            val bundle = Bundle().apply {
                putString("TABLE_TYPE", selectedItem.name)
            }
            findNavController().navigate(R.id.action_timeSlotManagementFragmentFragment_to_timeSlotDetailFragment, bundle)
        }

        binding.rvTimeSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = timeSlotAdapter
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.priceListsState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            LoadingUtils.show(requireContext())
                        }
                        is Resource.Success -> {
                            LoadingUtils.hide()
                            val priceLists = state.data

                            // Lọc danh sách: Đếm xem mỗi loại bàn (POOL, SNOOKER, CAROM) có bao nhiêu khung giờ (PriceList)
                            val poolCount = priceLists.count { it.tableType.equals("POOL", true) }
                            val snookerCount = priceLists.count { it.tableType.equals("SNOOKER", true) }
                            val caromCount = priceLists.count { it.tableType.equals("CAROM", true) }

                            // Chuyển hóa thành UI Model để đổ lên View
                            val uiModels = listOf(
                                TableTypeTimeSlotUiModel("1", "POOL", "Phổ biến", "#2962FF", "", poolCount),
                                TableTypeTimeSlotUiModel("2", "SNOOKER", "Quốc tế", "#4CAF50", "", snookerCount),
                                TableTypeTimeSlotUiModel("3", "CAROM", "Pháp", "#9E9E9E", "", caromCount)
                            )
                            timeSlotAdapter.submitList(uiModels)
                        }
                        is Resource.Error -> {
                            LoadingUtils.hide()
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}