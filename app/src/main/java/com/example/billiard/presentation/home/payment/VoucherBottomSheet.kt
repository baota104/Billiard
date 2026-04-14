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
import com.example.billiard.domain.model.Voucher
import com.example.billiard.presentation.adapter.VoucherAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class VoucherBottomSheet(
    private val voucherList: List<Voucher>,
    private val currentlySelectedId: Long? = null, // Đã đổi sang Long
    private val onVoucherSelected: (Voucher) -> Unit
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
        binding.btnClose.setOnClickListener { dismiss() }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        voucherAdapter = VoucherAdapter { selectedVoucher ->
            onVoucherSelected(selectedVoucher)
            dismiss()
        }

        binding.rvVouchers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = voucherAdapter
            itemAnimator = null
        }
        voucherAdapter.submitList(voucherList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}