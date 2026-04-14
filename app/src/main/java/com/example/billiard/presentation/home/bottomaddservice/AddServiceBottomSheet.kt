package com.example.billiard.presentation.home.bottomaddservice

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetAddServiceBinding
import com.example.billiard.domain.model.CategoryType
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.model.ServiceItemUiModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddServiceBottomSheet(
    private val serviceItem: Product,
    private val categoryType: CategoryType, // NEW: Receive the CategoryType
    private val onConfirmAction: (Product, Int) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddServiceBinding? = null
    private val binding get() = _binding!!

    private var currentQuantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetAddServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupBaseInfo()
        setupDynamicUI()
        setupClickListeners()
    }

    private fun setupBaseInfo() {
        binding.tvName.text = serviceItem.name

        // Update Subtitle based on the Enum
        binding.tvCategory.text = when (categoryType) {
            CategoryType.RENTAL -> "Dịch vụ thuê"
            CategoryType.RETAIL -> "Đồ ăn / Thức uống"
            else -> "Sản phẩm"
        }

        binding.tvQuantity.text = currentQuantity.toString()
    }

    // Hàm "Biến hình" UI tùy theo loại dịch vụ
    private fun setupDynamicUI() {
        // Check the Enum type instead of an isRental boolean
        if (categoryType == CategoryType.RENTAL) {
            // --- GIAO DIỆN THUÊ DỊCH VỤ ---
            binding.tvInfoLabel.text = "Giờ bắt đầu"

            // Assuming your Product model has a 'price' or 'sellingPrice' property
            // Format it directly here since we removed the UiModel
            val formattedPrice = "%,dđ".format(serviceItem.sellingPrice.toInt()).replace(',', '.')
            binding.tvPrice.text = "$formattedPrice/h"

            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            binding.tvInfoValue.text = currentTime

            binding.btnConfirm.text = "Xác nhận thuê"

            // Optional: Hide +/- buttons for rental if quantity is always 1
            // binding.btnMinus.visibility = View.GONE
            // binding.btnPlus.visibility = View.GONE

        } else {
            // --- GIAO DIỆN MUA ĐỒ ĂN UỐNG ---
            binding.tvInfoLabel.text = "Tạm tính"

            val formattedPrice = "%,dđ".format(serviceItem.sellingPrice.toInt()).replace(',', '.')
            binding.tvPrice.text = formattedPrice

            updateSubtotalPrice()

            binding.btnConfirm.text = "Xác nhận thêm"
        }
    }

    private fun setupClickListeners() {
        binding.btnMinus.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                binding.tvQuantity.text = currentQuantity.toString()

                // FIXED: Use categoryType instead of isRental
                if (categoryType != CategoryType.RENTAL) {
                    updateSubtotalPrice()
                }
            }
        }

        binding.btnPlus.setOnClickListener {
            currentQuantity++
            binding.tvQuantity.text = currentQuantity.toString()

            // FIXED: Use categoryType instead of isRental
            if (categoryType != CategoryType.RENTAL) {
                updateSubtotalPrice()
            }
        }

        binding.btnConfirm.setOnClickListener {
            onConfirmAction(serviceItem, currentQuantity)
            dismiss()
        }
    }

    private fun updateSubtotalPrice() {
        val total = serviceItem.sellingPrice * currentQuantity
        val formattedTotal = "%,dđ".format(total).replace(',', '.')
        binding.tvInfoValue.text = formattedTotal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}