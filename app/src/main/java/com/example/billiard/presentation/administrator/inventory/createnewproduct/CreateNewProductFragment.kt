package com.example.billiard.presentation.administrator.inventory.createnewproduct

import android.net.Uri
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
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
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.FileUtils
import com.example.billiard.databinding.FragmentCreateNewProductBinding
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
import com.example.billiard.presentation.administrator.inventory.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class CreateNewProductFragment : BaseFragment<FragmentCreateNewProductBinding>(FragmentCreateNewProductBinding::inflate) {

    private val viewModel: ProductViewModel by viewModels()

    private var rawCategories = listOf<Category>()
    private var selectedImageFile: File? = null
    
    // Biến lưu tạm giá nhập và số lượng tồn để trả về cho màn Hóa đơn
    private var temporaryImportPrice: Double = 0.0
    private var temporaryStock: Int = 0

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            Glide.with(this).load(it).centerCrop().into(binding.imgProductPreview)
            binding.layoutEmptyImage.hide()
            selectedImageFile = FileUtils.uriToFile(requireContext(), it)
        }
    }

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.cardImageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val name = binding.edtProductName.text.toString().trim()
            val categoryName = binding.actCategory.text.toString().trim()
            val importPriceStr = binding.edtImportPrice.text.toString().trim()
            val sellingPriceStr = binding.edtSellPrice.text.toString().trim()
            val stockStr = binding.edtStock.text.toString().trim()

            if (name.isEmpty() || categoryName.isEmpty() || importPriceStr.isEmpty() || sellingPriceStr.isEmpty() || stockStr.isEmpty()) {
                showToast("Vui lòng điền đầy đủ các thông tin bắt buộc (kể cả số lượng)!")
                return@setOnClickListener
            }

            val importPrice = importPriceStr.toDoubleOrNull() ?: 0.0
            val sellingPrice = sellingPriceStr.toDoubleOrNull() ?: 0.0
            val initStock = stockStr.toIntOrNull() ?: 0

            temporaryImportPrice = importPrice
            temporaryStock = initStock

            val categoryId = rawCategories.find { it.categoryName.equals(categoryName, true) }?.id ?: 0

            val param = UpsertProductParam(
                id = 0,
                name = name,
                sellingPrice = sellingPrice,
                importPrice = importPrice,
                initStock = initStock, 
                categoryId = categoryId,
                imageFile = selectedImageFile
            )

            // Gọi API lưu Sản phẩm mới vào CSDL
            viewModel.upsertProduct(param)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                
                // Lấy danh sách danh mục để đổ vào Dropdown
                launch {
                    viewModel.categoriesState.collect { state ->
                        if (state is Resource.Success) {
                            rawCategories = state.data
                            val categoryNames = rawCategories.map { it.categoryName }.toTypedArray()
                            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames)
                            binding.actCategory.setAdapter(adapter)
                        }
                    }
                }

                // Lắng nghe trạng thái tạo sản phẩm
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                showToast("Tạo sản phẩm thành công!")
                                viewModel.resetActionState()
                                
                                // Ép kiểu data trả về thành đối tượng Product để lấy ID thật từ Server
                                val newProduct = state.data as? Product
                                val newProductId = newProduct?.id ?: -1

                                setFragmentResult("ADD_PRODUCT_REQUEST", bundleOf(
                                    "productId" to newProductId, // Đã gắn đúng ID từ Server
                                    "productName" to binding.edtProductName.text.toString(),
                                    "quantity" to temporaryStock,
                                    "importPrice" to temporaryImportPrice
                                ))

                                findNavController().popBackStack(R.id.createReceiptFragment, false)
                            }
                            is Resource.Error -> {
                                showToast(state.message)
                                viewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }
}