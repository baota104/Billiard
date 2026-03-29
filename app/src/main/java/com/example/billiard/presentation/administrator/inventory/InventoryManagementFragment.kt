package com.example.billiard.presentation.administrator.inventory

import android.app.DatePickerDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentInventoryManagementBinding
import com.example.billiard.domain.model.ImportHistoryUiModel
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.presentation.adapter.ImportHistoryAdapter
import com.example.billiard.presentation.adapter.InventoryAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.example.billiard.presentation.administrator.inventory.edit.EditProductBottomSheet
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InventoryManagementFragment : BaseFragment<FragmentInventoryManagementBinding>(FragmentInventoryManagementBinding::inflate) {

    // Khai báo 3 Adapter
    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var inventoryAdapter: InventoryAdapter
    private lateinit var historyAdapter: ImportHistoryAdapter // Thêm Adapter Lịch sử

    // Danh sách gốc chứa toàn bộ sản phẩm trong kho & Lịch sử
    private var allInventoryProducts = listOf<OrderServiceUiModel>()
    private var allImportHistory = listOf<ImportHistoryUiModel>() // Thêm List Lịch sử

    // Lưu trạng thái bộ lọc hiện tại của Kho
    private var currentFilterCategory = "ALL"
    private var currentSearchQuery = ""

    override fun setupViews() {
        // 1. Nút Back
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // 2. Nút Thêm sản phẩm (FAB)
        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryManagementFragment_to_createReceiptFragment)
        }

        // 3. Khởi tạo UI
        setupTabs()
        setupRecyclerViews()
        setupDatePickers() // Thiết lập sự kiện chọn ngày

        // 4. Xử lý ô Tìm kiếm
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            currentSearchQuery = text?.toString()?.trim() ?: ""
            applyFilters()
        }

        // 5. Nút Lọc (Tab Lịch sử)
        binding.btnFilterHistory.setOnClickListener {
            val startDate = binding.tvStartDate.text.toString()
            val endDate = binding.tvEndDate.text.toString()

            if (startDate == "Từ ngày" || endDate == "Đến ngày") {
                showToast("Vui lòng chọn khoảng thời gian để lọc!")
                return@setOnClickListener
            }
            showToast("Đang lọc dữ liệu từ $startDate đến $endDate...")
            // TODO: Bắn API lấy danh sách hóa đơn theo khoảng thời gian này
        }

        // 6. Đổ dữ liệu giả
        createMockData()
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
                        binding.layoutDateFilter.hide() // Ẩn ô chọn ngày

                        binding.edtSearch.hint = "Tìm kiếm sản phẩm..."
                        binding.edtSearch.setText("") // Xóa text tìm kiếm cũ
                    }
                    1 -> { // TAB LỊCH SỬ NHẬP
                        binding.rvHistory.show()
                        binding.layoutDateFilter.show() // Hiện ô chọn ngày

                        binding.rvInventory.hide()
                        binding.rvCategories.hide()
                        binding.fabAddProduct.hide()

                        binding.edtSearch.hint = "Tìm kiếm theo mã phiếu (VD: PN005)..."
                        binding.edtSearch.setText("") // Xóa text tìm kiếm cũ
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerViews() {
        // --- 1. Adapter Danh mục (Chips ngang) ---
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            currentFilterCategory = selectedCategory.id
            applyFilters()
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        // --- 2. Adapter Tồn kho ---
        inventoryAdapter = InventoryAdapter(
            onEditClick = { clickedProduct ->
                val editSheet = EditProductBottomSheet(
                    productToEdit = clickedProduct,
                    onSave = { updatedProduct, importPrice ->
                        showToast("Đã lưu thay đổi cho ${updatedProduct.name}")
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
                    val mutableList = allInventoryProducts.toMutableList()
                    mutableList.remove(product)
                    allInventoryProducts = mutableList
                    applyFilters()
                    showToast("Đã xóa ${product.name} khỏi kho hàng!")
                }
            }
        )
        binding.rvInventory.adapter = inventoryAdapter

        // --- 3. Adapter Lịch sử nhập hàng ---
        historyAdapter = ImportHistoryAdapter { clickedReceipt ->
            showToast("Xem chi tiết phiếu: ${clickedReceipt.receiptCode}")
            findNavController().navigate(R.id.action_inventoryManagementFragment_to_receiptDetailFragment)
        }
        binding.rvHistory.adapter = historyAdapter
    }

    private fun setupDatePickers() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Chọn "Từ ngày"
        binding.tvStartDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.tvStartDate.text = dateFormat.format(calendar.time)
                binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_title))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Chọn "Đến ngày"
        binding.tvEndDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                binding.tvEndDate.text = dateFormat.format(calendar.time)
                binding.tvEndDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_title))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    // Hàm Lọc Kép (Tồn kho & Lịch sử)
    private fun applyFilters() {
        val selectedTab = binding.tabLayout.selectedTabPosition

        if (selectedTab == 0) {
            // LỌC TỒN KHO
            var filteredList = allInventoryProducts

            if (currentFilterCategory != "ALL") {
                filteredList = filteredList.filter { it.category == currentFilterCategory }
            }

            if (currentSearchQuery.isNotEmpty()) {
                filteredList = filteredList.filter {
                    it.name.contains(currentSearchQuery, ignoreCase = true)
                }
            }
            inventoryAdapter.submitList(filteredList)

        } else {
            // LỌC LỊCH SỬ (Theo mã phiếu)
            if (currentSearchQuery.isNotEmpty()) {
                val filteredHistory = allImportHistory.filter {
                    it.receiptCode.contains(currentSearchQuery, ignoreCase = true)
                }
                historyAdapter.submitList(filteredHistory)
            } else {
                historyAdapter.submitList(allImportHistory)
            }
        }
    }

    private fun createMockData() {
        // --- MOCK DATA TỒN KHO ---
        allInventoryProducts = listOf(
            OrderServiceUiModel("1", "sp1", "Coca Cola Light", "Đồ uống", "", quantity = 45, unitPrice = 12000, startTime = null, endTime = "Lon 330ml"),
            OrderServiceUiModel("2", "sp2", "Bò khô xé sợi", "Đồ ăn", "", quantity = 3, unitPrice = 40000, startTime = null, endTime = "Gói 50g"),
            OrderServiceUiModel("3", "sp3", "Marlboro Gold", "Thuốc lá", "", quantity = 12, unitPrice = 33000, startTime = null, endTime = "Bao"),
            OrderServiceUiModel("4", "sp4", "Sting Dâu", "Đồ uống", "", quantity = 150, unitPrice = 15000, startTime = null, endTime = "Chai 330ml"),
            OrderServiceUiModel("5", "sp5", "Mì xào trứng", "Đồ ăn", "", quantity = 15, unitPrice = 35000, startTime = null, endTime = "Đĩa")
        )

        // --- MOCK DATA DANH MỤC ---
        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả"),
            TableCategoryUIModel(id = "Đồ ăn", name = "Đồ ăn"),
            TableCategoryUIModel(id = "Đồ uống", name = "Đồ uống"),
            TableCategoryUIModel(id = "Thuốc lá", name = "Thuốc lá")
        )

        // --- MOCK DATA LỊCH SỬ NHẬP ---
        allImportHistory = listOf(
            ImportHistoryUiModel(5, "25/10/2023 14:30", 2500000.0, "Nguyễn Văn A"),
            ImportHistoryUiModel(4, "24/10/2023 09:15", 840000.0, "Trần Thị B"),
            ImportHistoryUiModel(3, "22/10/2023 16:45", 1200000.0, "Nguyễn Văn A"),
            ImportHistoryUiModel(2, "20/10/2023 10:20", 3150000.0, "Lê Văn C")
        )

        // Đẩy dữ liệu lên UI
        categoryAdapter.submitList(categories)
        inventoryAdapter.submitList(allInventoryProducts)
        historyAdapter.submitList(allImportHistory)
    }

    override fun observeData() {}
}