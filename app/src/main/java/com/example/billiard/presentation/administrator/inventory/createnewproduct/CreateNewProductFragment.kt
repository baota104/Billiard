package com.example.billiard.presentation.administrator.inventory.createnewproduct
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentCreateNewProductBinding

class CreateNewProductFragment : BaseFragment<FragmentCreateNewProductBinding>(FragmentCreateNewProductBinding::inflate) {

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupDropdownCategory()

        // Xử lý nút Lưu
        binding.btnSave.setOnClickListener {
            val name = binding.edtProductName.text.toString().trim()
            val category = binding.actCategory.text.toString().trim()
            val importPrice = binding.edtImportPrice.text.toString().trim()
            val sellPrice = binding.edtSellPrice.text.toString().trim()
            val unit = binding.edtUnit.text.toString().trim()

            // Validate nhanh
            if (name.isEmpty() || category.isEmpty() || importPrice.isEmpty() || sellPrice.isEmpty()) {
                showToast("Vui lòng điền đầy đủ các thông tin bắt buộc!")
                return@setOnClickListener
            }

            showToast("Tạo sản phẩm thành công!")
            findNavController().popBackStack(R.id.createReceiptFragment, false)
        }

        // Xử lý nút tải ảnh
        binding.cardImageContainer.setOnClickListener {
            showToast("Mở thư viện ảnh...")
        }
    }

    private fun setupDropdownCategory() {
        val categories = arrayOf("Đồ uống", "Đồ ăn", "Thuốc lá", "Dịch vụ")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actCategory.setAdapter(adapter)
    }

    override fun observeData() {}
}