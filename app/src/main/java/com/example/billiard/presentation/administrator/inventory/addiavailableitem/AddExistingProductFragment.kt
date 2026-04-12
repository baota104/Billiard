package com.example.billiard.presentation.administrator.inventory.addiavailableitem

import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentAddExistingProductBinding
import com.example.billiard.domain.model.Product
import com.example.billiard.presentation.adapter.ProductSearchAdapter
import com.example.billiard.presentation.administrator.inventory.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddExistingProductFragment : BaseFragment<FragmentAddExistingProductBinding>(FragmentAddExistingProductBinding::inflate) {

    private lateinit var searchAdapter: ProductSearchAdapter
    private val productViewModel: ProductViewModel by viewModels()

    private var selectedProduct: Product? = null
    private var isAutoFillingText = false
    
    // Cờ đặc biệt để phân biệt "Lần gọi API tự động ban đầu của ViewModel" và "Thao tác gõ tay của người dùng"
    private var hasUserTyped = false

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.cardSelectedProduct.hide()
        setupSearchRecyclerView()
        setupAutoCalculation()

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            if (isAutoFillingText) return@doOnTextChanged 

            val query = text.toString().trim()
            if (query.isNotEmpty()) {
                hasUserTyped = true
                productViewModel.searchProducts(query)
            } else {
                hasUserTyped = false // Trống text thì reset lại cờ
                binding.cardSearchResults.hide()
                selectedProduct = null
                binding.cardSelectedProduct.hide()
            }
        }

        binding.btnCreateNewProduct.setOnClickListener {
            findNavController().navigate(R.id.action_addExistingProductFragment_to_createNewProductFragment)
        }

        binding.btnConfirmAdd.setOnClickListener {
            if (selectedProduct == null) {
                showToast("Vui lòng chọn một sản phẩm!")
                return@setOnClickListener
            }
            
            val quantity = binding.edtQuantity.text.toString().toIntOrNull() ?: 0
            val importPrice = binding.edtImportPrice.text.toString().toDoubleOrNull() ?: 0.0

            if (quantity <= 0 || importPrice <= 0) {
                showToast("Số lượng và giá nhập phải lớn hơn 0")
                return@setOnClickListener
            }

            // Gửi dữ liệu về lại màn CreateReceiptFragment (Bao gồm cả imageUrl để hiển thị)
            setFragmentResult("ADD_PRODUCT_REQUEST", bundleOf(
                "productId" to selectedProduct!!.id,
                "productName" to selectedProduct!!.name,
                "quantity" to quantity,
                "importPrice" to importPrice,
                "imageUrl" to selectedProduct!!.imageUrl // Bổ sung imageUrl
            ))

            findNavController().popBackStack()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productsState.collect { state ->
                    when (state) {
                        is Resource.Success -> {
                            val products = state.data.content
                            // SỬA LỖI: Chỉ hiển thị list nếu list không rỗng, KHÔNG ĐANG ĐIỀN CHỮ TỰ ĐỘNG, CHƯA CHỌN SP, VÀ NGƯỜI DÙNG ĐÃ GÕ TEXT THẬT SỰ!
                            if (products.isNotEmpty() && !isAutoFillingText && selectedProduct == null && hasUserTyped) {
                                searchAdapter.submitList(products)
                                binding.cardSearchResults.show()
                            } else {
                                binding.cardSearchResults.hide()
                            }
                        }
                        is Resource.Error -> {
                            binding.cardSearchResults.hide()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupSearchRecyclerView() {
        searchAdapter = ProductSearchAdapter { product ->
            selectedProduct = product
            
            isAutoFillingText = true
            binding.edtSearch.setText(product.name)
            binding.edtSearch.clearFocus()
            
            binding.edtSearch.post {
                isAutoFillingText = false
            }

            binding.cardSearchResults.hide()

            binding.cardSelectedProduct.show()
            binding.tvProductName.text = product.name
            binding.tvStockBadge.text = "Kho: ${product.stock} cái"
            binding.tvProductCode.text = "Mã: SP${product.id}"

            val fakeImportPrice = (product.sellingPrice * 0.7).toInt()
            binding.edtImportPrice.setText(fakeImportPrice.toString())

            binding.edtQuantity.setText("1")
            binding.edtQuantity.requestFocus()

            calculateTotal()
        }
        binding.rvSearchResults.adapter = searchAdapter
    }

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
}