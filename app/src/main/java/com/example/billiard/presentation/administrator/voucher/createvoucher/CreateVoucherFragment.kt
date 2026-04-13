package com.example.billiard.presentation.administrator.voucher.createvoucher

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentCreateVoucherBinding
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.presentation.administrator.voucher.VoucherViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class CreateVoucherFragment : BaseFragment<FragmentCreateVoucherBinding>(FragmentCreateVoucherBinding::inflate) {

    private val viewModel: VoucherViewModel by viewModels()

    // Nhận ID từ màn hình trước. Nếu = -1 là Tạo mới, > -1 là Chỉnh sửa
    private val voucherId: Long by lazy {
        arguments?.getLong("VOUCHER_ID", -1L) ?: -1L
    }

    // Biến lưu thời gian dạng ISO truyền lên Backend (Ko xài TimeZone UTC nữa theo yêu cầu)
    private var startTimeIso: String = ""
    private var endTimeIso: String = ""

    // Format ngày chuẩn được yêu cầu "2026-04-13T20:00:00"
    private val sdfIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    private val sdfUi = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupDropdown()
        setupDatePickers()

        if (voucherId == -1L) {
            setupUIForCreate()
        } else {
            // Chế độ sửa: Gọi API lấy thông tin chi tiết Voucher
            viewModel.getVoucherById(voucherId)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Lắng nghe dữ liệu khi lấy chi tiết Voucher (Dành cho chế độ Sửa)
                launch {
                    viewModel.voucherDetailState.collect { state ->
                        if (state is Resource.Success) {
                            val voucher = state.data
                            setupUIForEdit(voucher)
                        } else if (state is Resource.Error) {
                            Toast.makeText(requireContext(), "Lỗi tải voucher: ${state.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // 2. Lắng nghe trạng thái khi bấm Lưu hoặc Xóa
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Thao tác thành công!", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                                findNavController().popBackStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Lỗi: ${state.message}", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setupDropdown() {
        // Map với Backend VoucherType: AMOUNT (Giảm cố định VNĐ), PERCENTAGE (Giảm theo % - VD Backend cho phép)
        val discountTypes = arrayOf("Giảm cố định (VNĐ)", "Giảm theo phần trăm (%)")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, discountTypes)
        binding.actDiscountType.setAdapter(adapter)
        binding.actDiscountType.setText(discountTypes[0], false)
    }

    private fun setupDatePickers() {
        // Xử lý chọn ngày bắt đầu
        binding.edtStartTime.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày bắt đầu")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Hiển thị giao diện người dùng
                binding.edtStartTime.setText(sdfUi.format(Date(selection)))

                // Gán giờ là 00:00:00 cho StartTime
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
                startTimeIso = "${dateStr}T00:00:00"
            }
            datePicker.show(childFragmentManager, "VoucherStartDatePicker")
        }

        // Xử lý chọn ngày kết thúc
        binding.edtExpiry.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày hết hạn")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                // Hiển thị giao diện người dùng
                binding.edtExpiry.setText(sdfUi.format(Date(selection)))

                // Gán giờ là 23:59:59 cho EndTime
                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
                endTimeIso = "${dateStr}T23:59:59"
            }
            datePicker.show(childFragmentManager, "VoucherEndDatePicker")
        }
    }

    private fun setupUIForCreate() {
        binding.tvHeaderTitle.text = "Tạo voucher mới"
        binding.btnPrimaryAction.text = "Tạo voucher"
        binding.btnDelete.visibility = View.GONE

        // Khởi tạo rỗng
        binding.edtCode.setText("")
        binding.edtValue.setText("")
        binding.edtMinOrder.setText("")
        binding.edtStartTime.setText("")
        binding.edtExpiry.setText("")
        binding.edtLimit.setText("")
        
        binding.switchActive.isChecked = true

        binding.btnPrimaryAction.setOnClickListener {
            val code = binding.edtCode.text.toString().trim()
            val valueStr = binding.edtValue.text.toString().trim()
            val minOrderStr = binding.edtMinOrder.text.toString().trim()
            val limitStr = binding.edtLimit.text.toString().trim()

            if (code.isEmpty() || valueStr.isEmpty() || minOrderStr.isEmpty() || limitStr.isEmpty() || startTimeIso.isEmpty() || endTimeIso.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ các thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val type = if (binding.actDiscountType.text.toString().contains("%")) "PERCENTAGE" else "AMOUNT"

            val param = CreateVoucherParam(
                voucherType = type,
                value = valueStr.toDouble(),
                source = "MANUAL", // Theo yêu cầu của giao diện bạn gửi
                status = if (binding.switchActive.isChecked) "ACTIVE" else "INACTIVE",
                startTime = startTimeIso,
                endTime = endTimeIso,
                quantity = limitStr.toInt(),
                minimumAmount = minOrderStr.toDouble(),
                maximumValue = null // Chỉ dùng khi là PERCENTAGE muốn giới hạn tối đa
            )

            viewModel.createVoucher(param)
        }
    }

    private fun setupUIForEdit(voucher: com.example.billiard.domain.model.Voucher) {
        binding.tvHeaderTitle.text = "Chỉnh sửa voucher"
        binding.btnPrimaryAction.text = "Lưu thay đổi"
        binding.btnDelete.visibility = View.VISIBLE

        binding.edtCode.setText(voucher.code)
        binding.edtCode.isEnabled = false // Không cho phép đổi Mã code sau khi tạo

        binding.edtValue.setText(voucher.value.toInt().toString())
        binding.edtValue.isEnabled = false // Không cho đổi giá trị sau khi tạo

        binding.edtMinOrder.setText(voucher.minimumAmount.toInt().toString())
        binding.edtMinOrder.isEnabled = false

        binding.edtLimit.setText(voucher.quantity.toString())

        // Xử lý Ngày (Đọc chuỗi "2026-04-13T20:00:00" cắt lấy text hiện UI)
        try {
            val dateStart = sdfIso.parse(voucher.startTime)
            if (dateStart != null) binding.edtStartTime.setText(sdfUi.format(dateStart))
            
            val dateEnd = sdfIso.parse(voucher.endTime)
            if (dateEnd != null) binding.edtExpiry.setText(sdfUi.format(dateEnd))
        } catch (e: Exception) {
            // Hiển thị chay nếu bị lỗi format từ DB
            binding.edtStartTime.setText(voucher.startTime.take(10)) 
            binding.edtExpiry.setText(voucher.endTime.take(10))
        }
        
        // Lưu lại chuẩn ISO cũ nếu user không chọn lại ngày mới
        startTimeIso = voucher.startTime
        endTimeIso = voucher.endTime

        val typeText = if (voucher.voucherType == "PERCENTAGE") "Giảm theo phần trăm (%)" else "Giảm cố định (VNĐ)"
        binding.actDiscountType.setText(typeText, false)
        binding.tilDiscountType.isEnabled = false

        binding.switchActive.isChecked = voucher.status.equals("ACTIVE", ignoreCase = true)

        // Sự kiện Cập nhật
        binding.btnPrimaryAction.setOnClickListener {
            // Theo API PUT UpdateVoucherRequest: Chỉ cho phép cập nhật status, start, end, quantity
            val newQuantityStr = binding.edtLimit.text.toString().trim()
            if (newQuantityStr.isEmpty()) return@setOnClickListener

            val param = UpdateVoucherParam(
                id = voucher.id,
                status = if (binding.switchActive.isChecked) "ACTIVE" else "INACTIVE",
                startTime = startTimeIso,
                endTime = endTimeIso,
                quantity = newQuantityStr.toInt()
            )
            viewModel.updateVoucher(param)
        }

        // Sự kiện Xóa
        binding.btnDelete.setOnClickListener {
            viewModel.deleteVoucher(voucher.id)
        }
    }
}