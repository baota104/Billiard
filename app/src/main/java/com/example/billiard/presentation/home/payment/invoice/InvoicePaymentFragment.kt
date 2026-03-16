package com.example.billiard.presentation.home.payment.invoice


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentInvoicePaymentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoicePaymentFragment : BaseFragment<FragmentInvoicePaymentBinding>(FragmentInvoicePaymentBinding::inflate) {

    override fun setupViews() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Tích hợp chức năng COPY (Sao chép số tài khoản)
        binding.btnCopyAccount.setOnClickListener {
            val accountNumber = binding.tvAccountNumber.text.toString()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Số tài khoản", accountNumber)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(requireContext(), "Đã sao chép: $accountNumber", Toast.LENGTH_SHORT).show()
        }

        binding.btnConfirmFinish.setOnClickListener {
            Toast.makeText(requireContext(), "Giao dịch thành công. Đóng bàn!", Toast.LENGTH_SHORT).show()
            // TODO: Bắn API chuyển trạng thái bàn thành EMPTY, sau đó quay về Màn hình chính
            // findNavController().navigate(R.id.action_payment_to_home)
        }

        bindMockData()
    }

    private fun bindMockData() {
        // Dữ liệu mô phỏng theo thiết kế
        binding.tvInvoiceId.text = "#INV-2026-0222-001"
        binding.tvBankName.text = "Vietcombank"
        binding.tvAccountName.text = "NGUYEN VAN A"
        binding.tvAccountNumber.text = "0123456789"

        // Format tổng tiền
        val totalAmount = 400000
        binding.tvTotalAmount.text = "%,d VND".format(totalAmount).replace(',', '.')

        // Gắn ngày giờ hiện tại
        val currentTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.tvTimestamp.text = "Thời gian lập: $currentTime"

        // TODO: Load ảnh mã QR động bằng thư viện (VD: ZXing hoặc load url từ API bằng Glide)
        // Glide.with(this).load("url_qr_code").into(binding.imgQR)
    }

    override fun observeData() {
        // ViewModel logic...
    }
}