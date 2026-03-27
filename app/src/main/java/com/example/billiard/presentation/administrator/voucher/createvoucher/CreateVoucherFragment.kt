package com.example.billiard.presentation.administrator.voucher.createvoucher

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentCreateVoucherBinding
import com.example.billiard.domain.model.VoucherUiModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CreateVoucherFragment : BaseFragment<FragmentCreateVoucherBinding>(FragmentCreateVoucherBinding::inflate) {

    private var voucherToEdit: VoucherUiModel =
        VoucherUiModel(
        "1", "SAVE20", "Giảm cố định (VNĐ)", "100000", "Hết hạn: 31/03/2026",
        isAiRecommended = true, isActive = true
    )

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupDropdown()
        setupDatePicker()
        setupUIByMode()
    }

    private fun setupUIByMode() {
        if (voucherToEdit == null) {
            // ================= CHẾ ĐỘ: TẠO MỚI =================
            binding.tvHeaderTitle.text = "Tạo voucher mới"
            binding.btnPrimaryAction.text = "Tạo voucher"

            binding.btnDelete.visibility = View.GONE

            // Xóa rỗng các ô nhập liệu (Đề phòng)
            binding.edtCode.setText("")
            binding.edtValue.setText("")
            binding.edtMinOrder.setText("")
            binding.edtExpiry.setText("")
            binding.edtLimit.setText("")
            binding.switchActive.isChecked = true
            binding.switchAI.isChecked = false

            // Sự kiện nút Tạo
            binding.btnPrimaryAction.setOnClickListener {
                Toast.makeText(requireContext(), "Tạo Voucher thành công!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

        } else {
            // ================= CHẾ ĐỘ: CHỈNH SỬA =================
            val voucher = voucherToEdit!!

            binding.tvHeaderTitle.text = "Chỉnh sửa voucher"
            binding.btnPrimaryAction.text = "Lưu thay đổi"

            // Ẩn nút Hủy xám, Hiện nút Xóa đỏ
            binding.btnDelete.visibility = View.VISIBLE

            // Đổ dữ liệu cũ lên form
            binding.edtCode.setText(voucher.code)
            // Giả lập điền số tiền (Vì Model hiện tại đang lưu chuỗi "Giảm 20.000 đ", nên thực tế phải map lại cho chuẩn số)
            binding.edtValue.setText("20000")
            binding.edtMinOrder.setText("100000")
            binding.edtExpiry.setText(voucher.expiryText.replace("Hết hạn: ", ""))
            binding.edtLimit.setText("100") // Giả lập giới hạn 100 lần
            binding.switchActive.isChecked = voucher.isActive
            binding.switchAI.isChecked = voucher.isAiRecommended

            // Khóa không cho sửa Mã Voucher (Tuỳ logic nghiệp vụ của bạn)
            // binding.edtCode.isEnabled = false

            // Sự kiện nút Lưu
            binding.btnPrimaryAction.setOnClickListener {
                Toast.makeText(requireContext(), "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }

            // Sự kiện nút Xóa
            binding.btnDelete.setOnClickListener {
                // TODO: Nên hiển thị một Dialog xác nhận trước khi xóa thật
                Toast.makeText(requireContext(), "Đã xóa voucher ${voucher.code}", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun setupDropdown() {
        val discountTypes = arrayOf("Giảm cố định (VNĐ)", "Giảm theo phần trăm (%)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, discountTypes)
        binding.actDiscountType.setAdapter(adapter)
        binding.actDiscountType.setText(discountTypes[0], false)
    }

    private fun setupDatePicker() {
        binding.edtExpiry.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày hết hạn")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = sdf.format(Date(selection))
                binding.edtExpiry.setText(formattedDate)
            }
            datePicker.show(childFragmentManager, "VoucherDatePicker")
        }
    }

    override fun observeData() {}
}