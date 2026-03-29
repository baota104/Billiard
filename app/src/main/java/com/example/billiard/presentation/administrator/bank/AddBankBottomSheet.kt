package com.example.billiard.presentation.administrator.bank

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.BottomSheetAddBankBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddBankBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddBankBinding? = null
    private val binding get() = _binding!!

    // Callback trả dữ liệu về BankFragment khi lưu thành công
    var onBankAdded: ((bankName: String, accountNum: String, accountHolder: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdown()
        setupInputs()

        binding.btnConfirmSave.setOnClickListener {
            val bankName = binding.actBankSelection.text.toString().trim()
            val accNumber = binding.edtAccountNumber.text.toString().trim()
            val accHolder = binding.edtAccountHolder.text.toString().trim()

            // Validate nhanh
            if (bankName.isEmpty() || accNumber.isEmpty() || accHolder.isEmpty()) {
                showToast("Vui lòng điền đầy đủ thông tin!")
                return@setOnClickListener
            }

            // Gọi API lưu tài khoản...

            showToast("Đã thêm tài khoản thành công!")

            // Trả dữ liệu về Fragment cha
            onBankAdded?.invoke(bankName, accNumber, accHolder)

            // Tự động đóng Bottom Sheet
            dismiss()
        }
    }

    private fun setupDropdown() {
        // Danh sách giả lập
        val banks = arrayOf("Vietcombank", "Techcombank", "MB Bank", "VP Bank", "BIDV", "Agribank")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, banks)
        binding.actBankSelection.setAdapter(adapter)
    }

    private fun setupInputs() {
        // Ép Tên chủ tài khoản tự động viết hoa (ALL CAPS)
        binding.edtAccountHolder.filters = arrayOf(InputFilter.AllCaps())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}