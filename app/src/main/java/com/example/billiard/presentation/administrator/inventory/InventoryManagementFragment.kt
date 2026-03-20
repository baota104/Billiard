package com.example.billiard.presentation.administrator.inventory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentInventoryManagementBinding
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.presentation.adapter.InventoryAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.example.billiard.presentation.administrator.inventory.edit.EditProductBottomSheet
import com.google.android.material.tabs.TabLayout

class InventoryManagementFragment : BaseFragment<FragmentInventoryManagementBinding>(FragmentInventoryManagementBinding::inflate) {

    // Khai báo 2 Adapter
    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var inventoryAdapter: InventoryAdapter

    // Danh sách gốc chứa toàn bộ sản phẩm trong kho
    private var allInventoryProducts = listOf<OrderServiceUiModel>()

    // Lưu trạng thái bộ lọc hiện tại
    private var currentFilterCategory = "ALL"
    private var currentSearchQuery = ""

    override fun setupViews() {
        // 1. Nút Back
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryManagementFragment_to_createReceiptFragment)
        }

        setupTabs()
        setupRecyclerViews()

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            currentSearchQuery = text?.toString()?.trim() ?: ""
            applyFilters()
        }


        // 5. Đổ dữ liệu giả
        createMockData()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tồn kho"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Lịch sử"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.rvInventory.show()
                        binding.rvCategories.show()
                        binding.fabAddProduct.show()

                        binding.rvHistory.hide()

                        binding.edtSearch.hint = "Tìm kiếm sản phẩm..."
                    }
                    1 -> {
                        // SỬ DỤNG EXTENSION SHOW/HIDE TẠI ĐÂY 👇
                        binding.rvHistory.show()

                        binding.rvInventory.hide()
                        binding.rvCategories.hide()
                        binding.fabAddProduct.hide()

                        binding.edtSearch.hint = "Tìm kiếm phiếu nhập/xuất..."
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
            // Khi click vào 1 danh mục -> Lưu ID và tiến hành lọc
            currentFilterCategory = selectedCategory.id
            applyFilters()
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null // Tắt chớp nháy khi đổi tab
        }

        // --- 2. Adapter Tồn kho ---
        inventoryAdapter = InventoryAdapter(
                onEditClick = { clickedProduct ->
                    val editSheet = EditProductBottomSheet(
                        productToEdit = clickedProduct,
                        onSave = { updatedProduct, importPrice ->
                            // TẠI ĐÂY BẠN LÀM CHỦ LOGIC:
                            // 1. Gọi API cập nhật lên Server truyền updatedProduct và importPrice
                            // 2. Tìm vị trí của item cũ trong List nội bộ và thay thế bằng updatedProduct
                            // 3. Gọi applyFilters() để UI tự động vẽ lại thẻ với tên/giá mới

                            showToast("Đã lưu thay đổi cho ${updatedProduct.name}")
                        }
                    )
                    editSheet.show(childFragmentManager, "EditProductSheet")
                },
            onDeleteClick = { product ->
                showConfirmDialog(
                    title = "Xóa sản phẩm",
                    message = "Bạn có chắc chắn muốn xóa \"${product.name}\" khỏi kho không? Hành động này không thể hoàn tác.",
                    confirmButtonText = "Xóa ngay",
                    iconResId = R.drawable.ic_priority // Dùng icon cảnh báo
                ) {
                    // ĐOẠN CODE NÀY CHỈ CHẠY KHI NGƯỜI DÙNG BẤM "XÓA NGAY"

                    // 1. Xóa khỏi danh sách gốc
                    val mutableList = allInventoryProducts.toMutableList()
                    mutableList.remove(product)
                    allInventoryProducts = mutableList

                    // 2. Cập nhật lại UI (gọi hàm applyFilters để nó tự lọc và đẩy vào adapter)
                    applyFilters()

                    showToast("Đã xóa ${product.name} khỏi kho hàng!")

                }
            }
        )
        binding.rvInventory.adapter = inventoryAdapter
    }

    // Hàm Lọc Kép (Kết hợp Tìm kiếm Text + Bộ lọc Danh mục)
    private fun applyFilters() {
        var filteredList = allInventoryProducts

        // 1. Lọc theo Danh mục (Nút bấm ngang)
        if (currentFilterCategory != "ALL") {
            filteredList = filteredList.filter { it.category == currentFilterCategory }
        }

        // 2. Lọc theo Chữ (Thanh tìm kiếm)
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // Đẩy danh sách đã lọc vào Adapter để hiển thị
        inventoryAdapter.submitList(filteredList)
    }

    private fun createMockData() {
        // --- MOCK DATA TỒN KHO ---
        // Lưu ý cách tận dụng OrderServiceUiModel:
        // quantity = Tồn kho hiện tại | unitPrice = Giá bán | endTime = Đơn vị tính (Lon/Gói...) | category = Phân loại
        allInventoryProducts = listOf(
            OrderServiceUiModel("1", "sp1", "Coca Cola Light", "Đồ uống", "", quantity = 45, unitPrice = 12000, startTime = null, endTime = "Lon 330ml"),
            OrderServiceUiModel("2", "sp2", "Bò khô xé sợi", "Đồ ăn", "", quantity = 3, unitPrice = 40000, startTime = null, endTime = "Gói 50g"),
            OrderServiceUiModel("3", "sp3", "Marlboro Gold", "Thuốc lá", "", quantity = 12, unitPrice = 33000, startTime = null, endTime = "Bao"),
            OrderServiceUiModel("4", "sp4", "Sting Dâu", "Đồ uống", "", quantity = 150, unitPrice = 15000, startTime = null, endTime = "Chai 330ml"),
            OrderServiceUiModel("5", "sp5", "Mì xào trứng", "Đồ ăn", "", quantity = 15, unitPrice = 35000, startTime = null, endTime = "Đĩa")
        )

        // --- MOCK DATA DANH MỤC ---
        // Ở đây mình dùng id chính là tên category để lúc filter (it.category == currentFilterCategory) so sánh cho dễ
        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả"),
            TableCategoryUIModel(id = "Đồ ăn", name = "Đồ ăn"),
            TableCategoryUIModel(id = "Đồ uống", name = "Đồ uống"),
            TableCategoryUIModel(id = "Thuốc lá", name = "Thuốc lá")
        )

        // Đẩy dữ liệu lên UI
        categoryAdapter.submitList(categories)
        inventoryAdapter.submitList(allInventoryProducts)
    }

    override fun observeData() {
        // Chỗ này sau này sẽ dùng để observe LiveData/StateFlow từ ViewModel
    }
}