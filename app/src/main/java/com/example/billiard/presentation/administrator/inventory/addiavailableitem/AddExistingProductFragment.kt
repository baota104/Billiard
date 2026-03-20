package com.example.billiard.presentation.administrator.inventory.addiavailableitem

import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentAddExistingProductBinding
import com.example.billiard.domain.model.ServiceItemUiModel
import com.example.billiard.presentation.administrator.inventory.receipt.ProductSearchAdapter

class AddExistingProductFragment : BaseFragment<FragmentAddExistingProductBinding>(FragmentAddExistingProductBinding::inflate) {

    private lateinit var searchAdapter: ProductSearchAdapter

    // Mock dữ liệu kho hàng
    private val allProductsInStock = listOf(
        ServiceItemUiModel("1", "Gậy Bi-a Carbon Fury", 1850000, "", "c1", false, stock = 15),
        ServiceItemUiModel("2", "Gậy Bi-a Mit Đen", 1200000, "", "c1", false, stock = 8),
        ServiceItemUiModel("3", "Coca Cola (Lon)", 12000, "", "c2", false, stock = 45)
    )

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Khởi tạo List sản phẩm đã chọn (Mặc định ẩn)
        binding.cardSelectedProduct.hide()

        setupSearchRecyclerView()
        setupAutoCalculation()

        // Xử lý gõ phím tìm kiếm
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            val query = text.toString().trim()
            if (query.isNotEmpty()) {
                val filteredList = allProductsInStock.filter { it.name.contains(query, ignoreCase = true) }
                if (filteredList.isNotEmpty()) {
                    searchAdapter.submitList(filteredList)
                    binding.cardSearchResults.show() // Hiện list kết quả
                } else {
                    binding.cardSearchResults.hide() // Ẩn nếu không tìm thấy
                }
            } else {
                binding.cardSearchResults.hide() // Ẩn khi xóa trắng text
            }
        }
        binding.btnCreateNewProduct.setOnClickListener {
            findNavController().navigate(R.id.action_addExistingProductFragment_to_createNewProductFragment)
        }

        binding.btnConfirmAdd.setOnClickListener {
            showToast("Thêm vào phiếu thành công!")
            findNavController().popBackStack()
        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = ProductSearchAdapter { selectedProduct ->
            // KHI NGƯỜI DÙNG CLICK VÀO 1 SẢN PHẨM TRONG LIST:

            // 1. Đổi text ô tìm kiếm thành tên sản phẩm & Ẩn list thả xuống
            binding.edtSearch.setText(selectedProduct.name)
            binding.edtSearch.clearFocus()
            binding.cardSearchResults.hide()

            // 2. Hiện thẻ Card sản phẩm đã chọn và đổ dữ liệu vào
            binding.cardSelectedProduct.show()
            binding.tvProductName.text = selectedProduct.name
            binding.tvStockBadge.text = "Kho: ${selectedProduct.stock} cái"
            binding.tvProductCode.text = "Mã: SP00${selectedProduct.id}"

            // 3. Tự điền Giá bán dự kiến (lấy từ giá price của model)
            binding.edtSellPrice.setText(selectedProduct.price.toString())

            // 4. (Tùy chọn) Giả lập Giá nhập bằng 70% giá bán
            val fakeImportPrice = (selectedProduct.price * 0.7).toInt()
            binding.edtImportPrice.setText(fakeImportPrice.toString())

            // Mặc định focus vào ô Số lượng và để là 1
            binding.edtQuantity.setText("1")
            binding.edtQuantity.requestFocus()

            calculateTotal()
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

    // Các hàm tính toán giữ nguyên như cũ
    private fun setupAutoCalculation() {
        binding.edtQuantity.doOnTextChanged { _, _, _, _ -> calculateTotal() }
        binding.edtImportPrice.doOnTextChanged { _, _, _, _ -> calculateTotal() }
    }

    private fun calculateTotal() {
        val quantity = binding.edtQuantity.text.toString().toIntOrNull() ?: 0
        val importPrice = binding.edtImportPrice.text.toString().toIntOrNull() ?: 0
        val totalAmount = quantity.toLong() * importPrice.toLong()
        binding.tvTotalCalculated.text = "%,d đ".format(totalAmount).replace(',', '.')
    }

    override fun observeData() {}
}