package com.example.billiard.presentation.administrator.inventory.edit

import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.core.utils.FileUtils
import com.example.billiard.databinding.BottomSheetEditProductBinding
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class EditProductBottomSheet(
    private val productToEdit: Product? = null,
    private val categories: List<Category>, // Nhận list danh mục thật từ API để map Category ID
    private val onSave: (UpsertProductParam) -> Unit 
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEditProductBinding? = null
    private val binding get() = _binding!!

    private var selectedImageFile: File? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.imgAddIcon.hide()
            binding.tvAddText.hide()
            Glide.with(this).load(it).centerCrop().into(binding.imgProductPreview)

            selectedImageFile = FileUtils.uriToFile(requireContext(), it)
        }
    }

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
        fillData()
    }

    private fun setupHeader() {
        binding.btnClose.setOnClickListener { dismiss() }
        if (productToEdit == null) {
            binding.tvTitle.text = "Thêm sản phẩm mới"
            // Cho phép nhập số lượng tồn kho ban đầu khi tạo mới
            binding.edtStock.show()
            binding.lblStock.show()
        } else {
            binding.tvTitle.text = "Chỉnh sửa sản phẩm"
            // Khi sửa không cho nhập số lượng (Phải dùng chức năng nhập/xuất kho)
            binding.edtStock.hide()
            binding.lblStock.hide()
        }
    }

    private fun setupDropdownCategory() {
        // Lấy tên danh mục để đưa vào Spinner
        val categoryNames = categories.map { it.categoryName }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoryNames)
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

    private fun fillData() {
        if (productToEdit != null) {
            binding.edtProductName.setText(productToEdit.name)
            binding.actCategory.setText(productToEdit.categoryName, false)

            // API Product không có importPrice, giả lập để hiển thị
            val fakeImportPrice = (productToEdit.sellingPrice * 0.7).toInt()
            binding.edtImportPrice.setText(fakeImportPrice.toString())

            binding.edtSellPrice.setText(productToEdit.sellingPrice.toInt().toString())

            if (productToEdit.imageUrl.isNotEmpty()) {
                binding.imgAddIcon.hide()
                binding.tvAddText.hide()
                Glide.with(this)
                    .load(productToEdit.imageUrl)
                    .centerCrop()
                    .into(binding.imgProductPreview)
            }
        }
    }

    private fun setupActionButtons() {
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.cardImageContainer.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            val newName = binding.edtProductName.text.toString().trim()
            val newCategoryName = binding.actCategory.text.toString().trim()
            val newSellPrice = binding.edtSellPrice.text.toString().toDoubleOrNull() ?: 0.0
            val newImportPrice = binding.edtImportPrice.text.toString().toDoubleOrNull() ?: 0.0
            val newStock = binding.edtStock.text.toString().toIntOrNull() ?: 0

            if (newName.isEmpty() || newCategoryName.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tìm Category ID dựa vào tên Category được chọn
            val categoryId = categories.find { it.categoryName == newCategoryName }?.id ?: 0

            val param = UpsertProductParam(
                id = productToEdit?.id ?: 0,
                name = newName,
                sellingPrice = newSellPrice,
                importPrice = newImportPrice,
                initStock = if (productToEdit == null) newStock else productToEdit.stock, // Chỉ cập nhật stock nếu là tạo mới
                categoryId = categoryId,
                imageFile = selectedImageFile
            )

            onSave(param)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}