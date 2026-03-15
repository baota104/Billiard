package com.example.billiard.presentation.home.bottomaddservice

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetAddServiceBinding
import com.example.billiard.domain.model.ServiceItemUiModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddServiceBottomSheet(
    private val serviceItem: ServiceItemUiModel,
    private val onConfirmAction: (ServiceItemUiModel, Int) -> Unit // Trả về món và Số lượng
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



        // Tạm dùng categoryId làm subtitle (Thực tế nên truyền tên Category thật vào Model)
        binding.tvCategory.text = if(serviceItem.isRental) "Dịch vụ thuê" else "Đồ ăn / Thức uống"

        binding.tvQuantity.text = currentQuantity.toString()
    }

    // Hàm "Biến hình" UI tùy theo loại dịch vụ
    private fun setupDynamicUI() {
        if (serviceItem.isRental) {
            // --- GIAO DIỆN THUÊ DỊCH VỤ ---
            binding.tvInfoLabel.text = "Giờ bắt đầu"
            binding.tvPrice.text = serviceItem.formattedPrice + "/h"
            // Lấy giờ hiện tại (VD: 14:30)
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            binding.tvInfoValue.text = currentTime

            binding.btnConfirm.text = "Xác nhận thuê"
        } else {
            // --- GIAO DIỆN MUA ĐỒ ĂN UỐNG ---
            binding.tvInfoLabel.text = "Tạm tính"
            binding.tvPrice.text = serviceItem.formattedPrice
            updateSubtotalPrice() // Tính tiền lần đầu tiên

            binding.btnConfirm.text = "Xác nhận thêm"
        }
    }

    private fun setupClickListeners() {
        binding.btnMinus.setOnClickListener {
            if (currentQuantity > 1) {
                currentQuantity--
                binding.tvQuantity.text = currentQuantity.toString()
                if (!serviceItem.isRental) updateSubtotalPrice()
            }
        }

        binding.btnPlus.setOnClickListener {
            currentQuantity++
            binding.tvQuantity.text = currentQuantity.toString()
            if (!serviceItem.isRental) updateSubtotalPrice()
        }

        binding.btnConfirm.setOnClickListener {
            onConfirmAction(serviceItem, currentQuantity)
            dismiss()
        }
    }

    private fun updateSubtotalPrice() {
        val total = serviceItem.price * currentQuantity
        val formattedTotal = "%,dđ".format(total).replace(',', '.')
        binding.tvInfoValue.text = formattedTotal
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}