package com.example.billiard.presentation.payment

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetVoucherBinding
import com.example.billiard.domain.model.VoucherUiModel
import com.example.billiard.presentation.adapter.VoucherAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VoucherBottomSheet(
    private val currentlySelectedId: String? = null, // Nhận ID mã đang dùng từ màn hình Thanh toán
    private val onVoucherSelected: (VoucherUiModel) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetVoucherBinding? = null
    private val binding get() = _binding!!

    private lateinit var voucherAdapter: VoucherAdapter

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
        _binding = BottomSheetVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        binding.btnClose.setOnClickListener { dismiss() }

        setupRecyclerView()
        loadMockData()
    }

    private fun setupRecyclerView() {
        voucherAdapter = VoucherAdapter { selectedVoucher ->
            onVoucherSelected(selectedVoucher)
            dismiss()
        }

        binding.rvVouchers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = voucherAdapter
            itemAnimator = null // Tắt chớp nháy khi viền thay đổi màu xanh/xám
        }
    }

    private fun loadMockData() {
        val mockVouchers = listOf(
            VoucherUiModel("1", "SAVE20", "Giảm 20.000 đ", "Đơn tối thiểu:100.000 đ", "Hết hạn: 31/03/2026", isAiRecommended = true),
            VoucherUiModel("2", "FREESHIP", "Giảm 15.000 đ", "Đơn tối thiểu:50.000 đ", "Hết hạn: 28/02/2026"),
            VoucherUiModel("3", "SPRING3", "Giảm 30%", "Đơn tối thiểu:200.000 đ", "Hết hạn: 30/04/2026", isAiRecommended = true),
            VoucherUiModel("4", "FLASH5", "Giảm 50.000 đ", "Đơn tối thiểu:300.000 đ", "Hết hạn: 15/05/2026")
        )
        voucherAdapter.submitList(mockVouchers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}