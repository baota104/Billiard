package com.example.billiard.presentation.administrator.inventory.addiavailableitem

import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentAddExistingProductBinding
import com.example.billiard.domain.model.Product
import com.example.billiard.presentation.administrator.inventory.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddExistingProductFragment : BaseFragment<FragmentAddExistingProductBinding>(FragmentAddExistingProductBinding::inflate) {

    private val productViewModel: ProductViewModel by viewModels()

    private var selectedProduct: Product? = null
    
    // Nơi chứa toàn bộ sản phẩm đã fetch về
    private var allAvailableProducts: List<Product> = emptyList()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Khởi tạo mặc định ẩn Card thông tin chi tiết
        binding.cardSelectedProduct.hide()
        
        // Ẩn RecyclerView tìm kiếm cũ vì ta đã chuyển nó vào BottomSheet
        binding.cardSearchResults.hide()

        setupAutoCalculation()

        // Thay đổi behavior của ô text Search: Khi user bấm vào -> Bật BottomSheet
        // Không cho phép gõ trực tiếp lên edtSearch này nữa
        binding.edtSearch.isFocusable = false
        binding.edtSearch.isClickable = true
        binding.edtSearch.setOnClickListener {
            if (allAvailableProducts.isEmpty()) {
                showToast("Đang tải dữ liệu sản phẩm, vui lòng đợi...")
                return@setOnClickListener
            }
            openSearchBottomSheet()
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

            // Gửi dữ liệu về lại màn CreateReceiptFragment (kèm theo imageUrl)
            setFragmentResult("ADD_PRODUCT_REQUEST", bundleOf(
                "productId" to selectedProduct!!.id,
                "productName" to selectedProduct!!.name,
                "quantity" to quantity,
                "importPrice" to importPrice,
                "imageUrl" to selectedProduct!!.imageUrl 
            ))

            findNavController().popBackStack()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Tải list Product 1 lần khi mở màn hình
                productViewModel.productsState.collect { state ->
                    when (state) {
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            allAvailableProducts = state.data.content
                        }
                        is Resource.Error -> {
                            showToast(state.message)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun openSearchBottomSheet() {
        val bottomSheet = SearchProductBottomSheet(allAvailableProducts) { product ->
            // Callback khi user đã chọn 1 sản phẩm từ BottomSheet
            selectedProduct = product
            
            // Cập nhật text của ô tìm kiếm ảo
            binding.edtSearch.setText(product.name)

            // Hiện thẻ Card sản phẩm đã chọn và đổ dữ liệu vào
            binding.cardSelectedProduct.show()
            binding.tvProductName.text = product.name
            binding.tvStockBadge.text = "Kho: ${product.stock} cái"
            binding.tvProductCode.text = "Mã: SP${product.id}"
            
            // Xử lý hiển thị ảnh sản phẩm ngay lập tức khi click chọn xong
            if (product.imageUrl.isNotEmpty()) {
                Glide.with(this)
                    .load(product.imageUrl)
                    .centerCrop()
                    .error(R.drawable.img_ban) // Nếu URL hỏng thì dùng ảnh mặc định
                    .into(binding.imgProduct)
            } else {
                binding.imgProduct.setImageResource(R.drawable.img_ban)
            }

            // Giả lập Giá nhập bằng 70% giá bán
            val fakeImportPrice = (product.sellingPrice * 0.7).toInt()
            binding.edtImportPrice.setText(fakeImportPrice.toString())

            // Mặc định focus vào ô Số lượng và để là 1
            binding.edtQuantity.setText("1")
            binding.edtQuantity.requestFocus()

            calculateTotal()
        }
        bottomSheet.show(childFragmentManager, "SearchProductSheet")
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