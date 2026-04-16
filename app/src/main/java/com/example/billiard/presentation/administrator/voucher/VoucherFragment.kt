package com.example.billiard.presentation.administrator.voucher

import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentVoucherBinding
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.domain.model.Voucher
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.example.billiard.presentation.adapter.VoucherAdapter
import com.example.billiard.presentation.adapter.VoucherDeleteAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VoucherFragment : BaseFragment<FragmentVoucherBinding>(FragmentVoucherBinding::inflate) {

    private val viewModel: VoucherViewModel by viewModels()

    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var voucherAdapter: VoucherDeleteAdapter

    private var allVouchers = listOf<Voucher>()

    // Lưu trạng thái Lọc
    private var currentFilterId = "ALL" // ALL, ACTIVE, EXPIRED
    private var currentSearchQuery = ""

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddVoucher.setOnClickListener {
            // Có thể truyền một flag qua Bundle để nhận biết là chức năng "Tạo Mới"
            findNavController().navigate(R.id.action_voucherFragment_to_createVoucherFragment)
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchVouchers(text.toString())
        }

        setupRecyclerViews()
        
        // Refresh danh sách mỗi khi vào lại màn hình
        viewModel.loadActiveVouchers()
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Lắng nghe dữ liệu danh sách Voucher
                launch {
                    viewModel.vouchersState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                LoadingUtils.show(requireContext())
                                // Có thể hiển thị ProgressBar nếu cần
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                allVouchers = state.data
                                updateCategories() // Cập nhật số lượng đếm trên Tabs
                                applyFilters()     // Lọc lại và đổ vào Adapter
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

    private fun setupRecyclerViews() {
        // 1. Setup Tabs (Danh mục trạng thái)
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            filterByCategory(selectedCategory.id)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        // 2. Setup Danh sách Voucher
        voucherAdapter = VoucherDeleteAdapter(
            onVoucherClick = { selectedVoucher ->
                // Xử lý khi click vào item (vd: chọn voucher)
            },
            onDeleteClick = { voucherToDelete ->
                // HIỂN THỊ DIALOG XÁC NHẬN KHI BẤM NÚT XÓA
                showConfirmDialog(
                    title = "Xác nhận xóa",
                    message = "Bạn có chắc chắn muốn xóa Voucher này?",

                    onConfirm = {
                        // XÓA VOUCHER
                        viewModel.deleteVoucher(voucherToDelete.id)
                    }
                )
            }
        )
        binding.rvVouchers.apply {
            adapter = voucherAdapter
        }
    }

    private fun updateCategories() {
        // Status ACTIVE coi như Đang hoạt động, khác ACTIVE coi như Bị khóa hoặc Hết hạn
        val activeCount = allVouchers.count { it.status.equals("ACTIVE", ignoreCase = true) }
        val expiredCount = allVouchers.count { !it.status.equals("ACTIVE", ignoreCase = true) }

        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả (${allVouchers.size})"),
            TableCategoryUIModel(id = "ACTIVE", name = "Đang hoạt động ($activeCount)"),
            TableCategoryUIModel(id = "EXPIRED", name = "Hết hạn ($expiredCount)")
        )
        categoryAdapter.submitList(categories)
    }

    private fun filterByCategory(categoryId: String) {
        currentFilterId = categoryId
        applyFilters()
    }

    private fun searchVouchers(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allVouchers

        // 1. Lọc theo Tab trạng thái
        if (currentFilterId != "ALL") {
            val isActiveTab = currentFilterId == "ACTIVE"
            filteredList = filteredList.filter { 
                val isItemActive = it.status.equals("ACTIVE", ignoreCase = true)
                isItemActive == isActiveTab
            }
        }

        // 2. Lọc theo chữ tìm kiếm (Tìm theo mã Code)
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { voucher ->
                voucher.code.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        voucherAdapter.submitList(filteredList)
    }
}