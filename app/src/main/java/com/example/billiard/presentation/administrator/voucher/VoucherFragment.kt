package com.example.billiard.presentation.administrator.voucher

import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentVoucherBinding
import com.example.billiard.domain.model.TableCategoryUIModel // Tái sử dụng Model Tab của bạn
import com.example.billiard.domain.model.VoucherUiModel
import com.example.billiard.presentation.adapter.TableCategoryAdapter // Tái sử dụng Adapter Tab của bạn
import com.example.billiard.presentation.adapter.VoucherAdapter

class VoucherFragment : BaseFragment<FragmentVoucherBinding>(FragmentVoucherBinding::inflate) {

    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var voucherAdapter: VoucherAdapter

    private var allVouchers = listOf<VoucherUiModel>()

    // Lưu trạng thái Lọc
    private var currentFilterId = "ALL" // ALL, ACTIVE, EXPIRED
    private var currentSearchQuery = ""

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddVoucher.setOnClickListener {
            Toast.makeText(requireContext(), "Mở màn thêm Voucher mới", Toast.LENGTH_SHORT).show()
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchVouchers(text.toString())
        }
        binding.fabAddVoucher.setOnClickListener {
                findNavController().navigate(R.id.action_voucherFragment_to_createVoucherFragment)
        }

        setupRecyclerViews()
        createMockData()
    }

    private fun setupRecyclerViews() {
        // 1. Setup Tabs (Dùng lại Adapter Category)
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            filterByCategory(selectedCategory.id)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        // 2. Setup Danh sách Voucher
        voucherAdapter = VoucherAdapter { selectedItem ->
            findNavController().navigate(R.id.action_voucherFragment_to_createVoucherFragment)
            Toast.makeText(requireContext(), "Sửa Voucher: ${selectedItem.code}", Toast.LENGTH_SHORT).show()
        }
        binding.rvVouchers.apply {
            adapter = voucherAdapter
            // Layout Manager đã được set trong XML
        }
    }

    // --- LOGIC LỌC TỔNG HỢP ---
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
            val isSearchingActive = currentFilterId == "ACTIVE"
            filteredList = filteredList.filter { it.isActive == isSearchingActive }
        }

        // 2. Lọc theo chữ tìm kiếm
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { voucher ->
                voucher.code.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        voucherAdapter.submitList(filteredList)
    }

    private fun createMockData() {
        // Dữ liệu y chang thiết kế
        allVouchers = listOf(
            VoucherUiModel("1", "SAVE20", "Giảm 20.000 đ", "Đơn tối thiểu:100.000 đ", "Hết hạn: 31/03/2026", isAiRecommended = true),
            VoucherUiModel("2", "FREESHIP", "Giảm 15.000 đ", "Đơn tối thiểu:50.000 đ", "Hết hạn: 28/02/2026",false,false),
            VoucherUiModel("3", "SPRING3", "Giảm 30%", "Đơn tối thiểu:200.000 đ", "Hết hạn: 30/04/2026", isAiRecommended = true,false),
            VoucherUiModel("4", "FLASH5", "Giảm 50.000 đ", "Đơn tối thiểu:300.000 đ", "Hết hạn: 15/05/2026")
        )

        val activeCount = allVouchers.count { it.isActive }
        val expiredCount = allVouchers.count { !it.isActive }

        // Đẩy tên Tab có kèm số lượng (VD: Tất cả (5))
        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả (${allVouchers.size})"),
            TableCategoryUIModel(id = "ACTIVE", name = "Đang hoạt động ($activeCount)"),
            TableCategoryUIModel(id = "EXPIRED", name = "Hết hạn ($expiredCount)")
        )
        categoryAdapter.submitList(categories)

        voucherAdapter.submitList(allVouchers)
    }

    override fun observeData() {}
}