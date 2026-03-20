package com.example.billiard.presentation.administrator.inventory.edit

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetEditProductBinding
import com.example.billiard.domain.model.OrderServiceUiModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProductBottomSheet(
    private val productToEdit: OrderServiceUiModel, // 1. Nhận item từ Fragment
    private val onSave: (OrderServiceUiModel, Int) -> Unit // 2. Callback trả về item mới và giá nhập
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        setupHeader()
        setupDropdownCategory()
        setupProfitCalculation()
        setupActionButtons()

        // Đổ dữ liệu thật từ constructor vào UI
        fillData()
    }

    private fun setupHeader() {
        binding.btnClose.setOnClickListener { dismiss() }
    }

    private fun setupDropdownCategory() {
        val categories = arrayOf("Đồ uống", "Đồ ăn", "Thuốc lá", "Dịch vụ")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupProfitCalculation() {
        val profitWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateProfit()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.edtImportPrice.addTextChangedListener(profitWatcher)
        binding.edtSellPrice.addTextChangedListener(profitWatcher)
    }

    private fun calculateProfit() {
        val importPriceStr = binding.edtImportPrice.text.toString()
        val sellPriceStr = binding.edtSellPrice.text.toString()

        if (importPriceStr.isEmpty() || sellPriceStr.isEmpty()) {
            binding.tvProfitAmount.text = "0đ (0%)"
            return
        }

        try {
            val importPrice = importPriceStr.toInt()
            val sellPrice = sellPriceStr.toInt()

            val profitAmount = sellPrice - importPrice
            val formattedProfit = "%,d".format(profitAmount).replace(',', '.')

            if (sellPrice == 0) {
                binding.tvProfitAmount.text = "+$formattedProfit (0%)"
                return
            }
            val profitPercent = (profitAmount.toFloat() / sellPrice.toFloat() * 100).toInt()

            binding.tvProfitAmount.text = "+$formattedProfit ($profitPercent%)"

        } catch (e: NumberFormatException) {
            binding.tvProfitAmount.text = "Error"
        }
    }

    // Đổ dữ liệu của productToEdit lên giao diện
    private fun fillData() {
        binding.edtProductName.setText(productToEdit.name)
        binding.actCategory.setText(productToEdit.category, false)

        // Do model mượn không có giá nhập, ta giả lập giá nhập = 70% giá bán để hiển thị
        val fakeImportPrice = (productToEdit.unitPrice * 0.7).toInt()
        binding.edtImportPrice.setText(fakeImportPrice.toString())

        binding.edtSellPrice.setText(productToEdit.unitPrice.toString())
    }

    private fun setupActionButtons() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSave.setOnClickListener {
            // Lấy dữ liệu mới từ các ô nhập
            val newName = binding.edtProductName.text.toString().trim()
            val newCategory = binding.actCategory.text.toString().trim()
            val newSellPrice = binding.edtSellPrice.text.toString().toIntOrNull() ?: 0
            val newImportPrice = binding.edtImportPrice.text.toString().toIntOrNull() ?: 0

            if (newName.isEmpty() || newCategory.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo một bản sao chép của Item với dữ liệu mới
            val updatedProduct = productToEdit.copy(
                name = newName,
                category = newCategory,
                unitPrice = newSellPrice
            )

            // Bắn dữ liệu về cho Fragment thông qua callback
            onSave(updatedProduct, newImportPrice)
            dismiss()
        }

        binding.cardImageContainer.setOnClickListener {
            // Logic chọn ảnh
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}