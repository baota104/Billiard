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
import com.example.billiard.domain.model.CreateBankParam
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddBankBottomSheet(
    private val onBankAdded: (CreateBankParam) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddBankBinding? = null
    private val binding get() = _binding!!

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

            if (bankName.isEmpty() || accNumber.isEmpty() || accHolder.isEmpty()) {
                showToast("Vui lòng điền đầy đủ thông tin!")
                return@setOnClickListener
            }

            // Tạm thời map tĩnh các BIN. Trong ứng dụng thực tế, nên gọi API VietQR để lấy danh sách BIN chuẩn
            val bankBin = when (bankName.uppercase()) {
                "VIETCOMBANK" -> "970436"
                "TECHCOMBANK" -> "970407"
                "MB BANK" -> "970422"
                "VP BANK", "VPBANK" -> "970432"
                "BIDV" -> "970418"
                "AGRIBANK" -> "970405"
                "VIETINBANK" -> "970415"
                "ACB" -> "970416"
                else -> "970400" // Mặc định nếu không tìm thấy
            }

            val param = CreateBankParam(
                bankBin = bankBin,
                bankAccountNo = accNumber,
                bankAccountName = accHolder,
                bankStatus = false, // Mặc định tạo ra chưa được chọn làm phương thức thanh toán ưu tiên
                bankName = bankName,
                bankShortName = bankName.split(" ").firstOrNull() ?: bankName,
                bankLogo = "" // Nếu muốn, bạn có thể truyền link ảnh cứng tại đây, hoặc API backend tự bù
            )

            onBankAdded(param)
            dismiss()
        }
    }

    private fun setupDropdown() {
        val banks = arrayOf("Vietcombank", "Techcombank", "MB Bank", "VP Bank", "BIDV", "Agribank", "Vietinbank", "ACB")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, banks)
        binding.actBankSelection.setAdapter(adapter)
    }

    private fun setupInputs() {
        binding.edtAccountHolder.filters = arrayOf(InputFilter.AllCaps())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}