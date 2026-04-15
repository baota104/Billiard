package com.example.billiard.presentation.home.payment.invoice

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentInvoicePaymentBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint // BẮT BUỘC PHẢI CÓ
class InvoicePaymentFragment : BaseFragment<FragmentInvoicePaymentBinding>(FragmentInvoicePaymentBinding::inflate) {

    private val viewModel: InVoicePaymentViewModel by viewModels()

    private var currentInvoice: Invoice? = null
    private var finalTotal: Float = 0.0F
    private var appliedVoucherId: Long = -1L
    private var bankId: Int = -1

    override fun setupViews() {
        val invoiceJson = arguments?.getString("INVOICE_JSON")
        finalTotal = arguments?.getFloat("FINAL_TOTAL") ?: 0.0F
        appliedVoucherId = arguments?.getLong("VOUCHER_ID") ?: -1L
        bankId = arguments?.getInt("BANK_ID") ?: -1

        // Dịch ngược JSON về đối tượng Invoice
        if (!invoiceJson.isNullOrEmpty()) {
            currentInvoice = Gson().fromJson(invoiceJson, Invoice::class.java)

            // 1. Hiển thị thông tin
            bindRealData()

            // 2. Gọi API lấy mã QR ngay lập tức
            if (bankId != -1) {
                viewModel.loadQrCode(currentInvoice!!.id)
            } else {
                Toast.makeText(requireContext(), "Lỗi: Không tìm thấy ID Ngân hàng", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Lỗi tải dữ liệu hóa đơn", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        setupClickListeners()
    }
    private fun confirmExitPayment() {
        showConfirmDialog(
            title = "Hủy thanh toán?",
            message = "Giao dịch chưa hoàn tất, bạn có chắc chắn muốn quay lại không?",
            confirmButtonText = "Thoát",
            cancelButtonText = "Ở lại"
        ) {
            findNavController().popBackStack()
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
           confirmExitPayment()
        }


        binding.btnConfirmFinish.setOnClickListener {
            currentInvoice?.let { invoice ->
                // Ép kiểu ID Voucher (Nếu bằng -1 tức là không dùng voucher -> null)
                val vId: Int? = if (appliedVoucherId != -1L) appliedVoucherId.toInt() else null

                // Tạo Param để gọi API Update
                val param = UpdateInvoiceParam(
                    id = invoice.id,
                    startTime = parseIsoDate(invoice.startTime), // Dịch String -> Date
                    endTime = Date(), // Chốt giờ kết thúc là thời điểm bấm thanh toán
                    status = "PAID",
                    paymentMethod = "CREDIT_CARD", // Chuyển khoản (Có thể truyền động nếu bạn có nút chọn tiền mặt/chuyển khoản)
                    serviceAmount = invoice.serviceAmount ?: 0.0,
                    productAmount = invoice.productAmount ?: 0.0,
                    taxAmount = invoice.taxAmount,
                    totalAmount = finalTotal.toDouble(), // Sử dụng tổng tiền ĐÃ ÁP MÃ từ màn trước
                    voucherId = vId,
                    employeeId = invoice.employeeId,
                    billiardTableId = invoice.billiardTableId
                )

                viewModel.completePayment(param)
            }
        }
    }

    private fun bindRealData() {
        currentInvoice?.let { invoice ->
            binding.tvInvoiceId.text = "#INV-${invoice.id}"

            binding.tvTotalAmount.text = "%,d VND".format(finalTotal.toLong()).replace(',', '.')

            val currentTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date())
            binding.tvTimestamp.text = "Thời gian lập: $currentTime"
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Lắng nghe API load mã QR
                launch {
                    viewModel.qrState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                Log.d("InvoicePayment", "Đang tải mã QR...")
                                // (Tùy chọn) Hiện ProgressBar xoay xoay ở chỗ mã QR
                                LoadingUtils.show(requireContext())
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                // Lấy đối tượng VietQr từ API
                                val vietQrData = state.data

                                // Dùng Glide load link ảnh vào ImageView
                                Glide.with(requireContext())
                                    .load(vietQrData.qrImageUrl)
                                    .placeholder(R.drawable.ic_launcher_background) // Ảnh mặc định lúc đang tải
                                    .error(R.drawable.ic_launcher_foreground) // Ảnh lỗi nếu link hỏng
                                    .into(binding.imgQR)

                                Log.d("InvoicePayment", "Load QR thành công: ${vietQrData.qrImageUrl}")
                            }
                            is Resource.Error -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Không tải được mã QR: ${state.message}", Toast.LENGTH_SHORT).show()
                            }
                            null -> {}
                        }
                    }
                }

                // 2. Lắng nghe API chốt hóa đơn (Đóng bàn)
                launch {
                    viewModel.paymentState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                // TODO: (Tùy chọn) Hiện loading dialog
                                Log.d("InvoicePayment", "Đang xử lý thanh toán...")
                            }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Giao dịch thành công. Đã đóng bàn!", Toast.LENGTH_SHORT).show()
                                navigateToHomeAndClearStack()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), "Lỗi đóng bàn: ${state.message}", Toast.LENGTH_LONG).show()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }
    private fun navigateToHomeAndClearStack() {
        // Điều hướng về HomeFragment và xóa toàn bộ Backstack phía trước
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.homeFragment, true) // Xóa sạch từ HomeFragment trở đi
            .build()

        // Hoặc đơn giản hơn nếu Action đã được cấu hình popUpTo trong nav_graph
        findNavController().navigate(R.id.homeFragment, null, navOptions)
    }

    // --- HÀM PHỤ TRỢ DỊCH CHUỖI SANG DATE ---
    private fun parseIsoDate(dateString: String?): Date? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            format.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}