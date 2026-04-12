package com.example.billiard.presentation.administrator.inventory

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentInventoryManagementBinding
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.presentation.adapter.ImportHistoryAdapter
import com.example.billiard.presentation.adapter.InventoryAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.example.billiard.presentation.administrator.inventory.edit.EditProductBottomSheet
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class InventoryManagementFragment : BaseFragment<FragmentInventoryManagementBinding>(FragmentInventoryManagementBinding::inflate) {

    // ViewModel cho Tab 1 (Sản phẩm)
    private val productViewModel: ProductViewModel by viewModels()
    
    // ViewModel cho Tab 2 (Lịch sử nhập)
    private val purchaseViewModel: PurchaseViewModel by viewModels()

    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var historyAdapter: ImportHistoryAdapter

    private var allInventoryProducts = listOf<Product>()
    private var allImportHistory = listOf<PurchaseHistory>() 

    private var rawCategories = listOf<Category>() 

    private var currentFilterCategoryId = "ALL"
    private var currentSearchQuery = ""

    // Sửa format thành "yyyy-MM-dd" để khớp thiết kế của Backend
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    // Format hiển thị trên UI
    private val uiDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Biến lưu trữ thời gian lọc thực tế để gọi API
    private var filterStartDateIso = ""
    private var filterEndDateIso = ""

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryManagementFragment_to_createReceiptFragment)
        }

        setupTabs()
        setupRecyclerViews()
        setupDatePickers()

        // Lọc text theo tên tại bộ nhớ máy (Local) cho Tab Tồn Kho
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            currentSearchQuery = text?.toString()?.trim() ?: ""
            applyFilters()
        }

        // Bấm nút Lọc Lịch sử
        binding.btnFilterHistory.setOnClickListener {
            if (filterStartDateIso.isEmpty() || filterEndDateIso.isEmpty()) {
                showToast("Vui lòng chọn cả Từ ngày và Đến ngày để lọc!")
                return@setOnClickListener
            }
            purchaseViewModel.searchPurchaseInvoices(filterStartDateIso, filterEndDateIso)
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tồn kho"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Lịch sử nhập"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // TAB TỒN KHO
                        binding.rvInventory.show()
                        binding.rvCategories.show()
                        binding.fabAddProduct.show()

                        binding.rvHistory.hide()
                        binding.layoutDateFilter.hide()
                        
                        binding.edtSearch.show()

                        binding.edtSearch.hint = "Tìm kiếm sản phẩm..."
                        binding.edtSearch.setText("")
                    }
                    1 -> { // TAB LỊCH SỬ NHẬP
                        binding.rvHistory.show()
                        binding.layoutDateFilter.show()

                        binding.rvInventory.hide()
                        binding.rvCategories.hide()
                        binding.fabAddProduct.hide()
                        
                        // Ẩn thanh Search vì tab Lịch sử chỉ tìm qua DatePicker
                        binding.edtSearch.hide()
                        
                        // Gọi API lấy lịch sử ngay khi chuyển sang Tab này (nếu chưa có dữ liệu)
                        if (allImportHistory.isEmpty() && filterStartDateIso.isNotEmpty() && filterEndDateIso.isNotEmpty()) {
                            purchaseViewModel.searchPurchaseInvoices(filterStartDateIso, filterEndDateIso)
                        }
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerViews() {
        // 1. Adapter Chips Categories
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            currentFilterCategoryId = selectedCategory.id
            applyFilters()
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        // 2. Adapter Inventory (Product)
        inventoryAdapter = InventoryAdapter(
            onEditClick = { clickedProduct ->
                val editSheet = EditProductBottomSheet(
                    productToEdit = clickedProduct,
                    categories = rawCategories,
                    onSave = { param ->
                        productViewModel.upsertProduct(param)
                    }
                )
                editSheet.show(childFragmentManager, "EditProductSheet")
            },
            onDeleteClick = { product ->
                showConfirmDialog(
                    title = "Xóa sản phẩm",
                    message = "Bạn có chắc chắn muốn xóa \"${product.name}\" khỏi kho không?",
                    confirmButtonText = "Xóa ngay",
                    iconResId = R.drawable.ic_priority
                ) {
                    productViewModel.deleteProduct(product.id)
                }
            }
        )
        binding.rvInventory.apply {
            adapter = inventoryAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                        if (!productViewModel.isLoadingMore && (visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            productViewModel.loadAllProducts()
                        }
                    }
                }
            })
        }

        // 3. Adapter Lịch sử Nhập hàng (PurchaseHistory)
        historyAdapter = ImportHistoryAdapter { clickedReceipt ->
            // Truyền ID sang màn hình Detail
            val bundle = Bundle().apply {
                putInt("INVOICE_ID", clickedReceipt.purchaseId)
            }
            findNavController().navigate(R.id.action_inventoryManagementFragment_to_receiptDetailFragment, bundle)
        }
        binding.rvHistory.adapter = historyAdapter
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                // --------- OBSERVE TAB TỒN KHO ---------
                launch {
                    productViewModel.categoriesState.collect { state ->
                        if (state is Resource.Success) {
                            rawCategories = state.data
                            val uiCategories = mutableListOf(TableCategoryUIModel("ALL", "Tất cả"))
                            uiCategories.addAll(rawCategories.map { 
                                TableCategoryUIModel(id = it.categoryName, name = it.categoryName)
                            })
                            categoryAdapter.submitList(uiCategories)
                        }
                    }
                }

                launch {
                    productViewModel.productsState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                allInventoryProducts = state.data.content
                                applyFilters()
                            }
                            is Resource.Error -> showToast(state.message)
                            null -> {}
                        }
                    }
                }

                launch {
                    productViewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                showToast("Thao tác sản phẩm thành công!")
                                productViewModel.resetActionState()
                            }
                            is Resource.Error -> {
                                showToast("Lỗi: ${state.message}")
                                productViewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
                
                // --------- OBSERVE TAB LỊCH SỬ ---------
                launch {
                    purchaseViewModel.purchaseHistoriesState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                allImportHistory = state.data
                                historyAdapter.submitList(allImportHistory)
                                if(allImportHistory.isEmpty()){
                                    showToast("Không tìm thấy hóa đơn nhập nào trong khoảng thời gian này.")
                                }
                            }
                            is Resource.Error -> {
                                showToast("Lỗi lấy lịch sử: ${state.message}")
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun applyFilters() {
        val selectedTab = binding.tabLayout.selectedTabPosition

        // Vì hiện tại tab Lịch sử đã có API Search lọc cứng theo ngày, nên applyFilters chỉ lọc cho Tab 0
        if (selectedTab == 0) {
            var filteredList = allInventoryProducts

            if (currentFilterCategoryId != "ALL") {
                filteredList = filteredList.filter { it.categoryName.equals(currentFilterCategoryId, true) }
            }

            if (currentSearchQuery.isNotEmpty()) {
                filteredList = filteredList.filter { 
                    it.name.contains(currentSearchQuery, ignoreCase = true) 
                }
            }

            inventoryAdapter.submitList(filteredList)
        }
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        
        // Mặc định set mốc thời gian lọc từ đầu tháng đến hiện tại
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        filterStartDateIso = apiDateFormat.format(calendar.time)
        binding.tvStartDate.text = uiDateFormat.format(calendar.time)
        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_title))
        
        calendar.time = java.util.Date() // Thời điểm hiện tại
        filterEndDateIso = apiDateFormat.format(calendar.time)
        binding.tvEndDate.text = uiDateFormat.format(calendar.time)
        binding.tvEndDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_title))

        // Chọn "Từ ngày"
        binding.tvStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                
                filterStartDateIso = apiDateFormat.format(cal.time)
                binding.tvStartDate.text = uiDateFormat.format(cal.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Chọn "Đến ngày"
        binding.tvEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance()
                cal.set(year, month, dayOfMonth)
                
                filterEndDateIso = apiDateFormat.format(cal.time)
                binding.tvEndDate.text = uiDateFormat.format(cal.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
}