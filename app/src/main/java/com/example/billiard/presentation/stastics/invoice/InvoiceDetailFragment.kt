package com.example.billiard.presentation.stastics.invoice

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentInvoiceDetailBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.presentation.adapter.OrderDetailAdapter
import com.example.billiard.presentation.invoice.InvoiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class InvoiceDetailFragment : BaseFragment<FragmentInvoiceDetailBinding>(FragmentInvoiceDetailBinding::inflate) {

    // Sử dụng InvoiceViewModel thay vì PaymentViewModel
    private val viewModel: InvoiceViewModel by viewModels()
    private lateinit var serviceAdapter: OrderDetailAdapter
    private var invoiceId: Int = -1

    override fun setupViews() {
        invoiceId = arguments?.getInt("INVOICE_ID") ?: -1

        if (invoiceId != -1) {
            // Gọi API lấy thông tin chi tiết hóa đơn
            viewModel.getInvoiceById(invoiceId)
        } else {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Hóa đơn", Toast.LENGTH_SHORT).show()
        }

        setupClick()
    }

    private fun setupClick() {
        // Chỉ giữ lại nút Back
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Setup danh sách món ăn / dịch vụ
        serviceAdapter = OrderDetailAdapter()
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Lắng nghe trạng thái Chi tiết Hóa Đơn
                viewModel.invoiceDetailState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            LoadingUtils.show(requireContext())
                        }
                        is Resource.Success -> {
                            LoadingUtils.hide()
                            val invoice = state.data
                            bindInvoiceData(invoice)
                        }
                        is Resource.Error -> {
                            LoadingUtils.hide()
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun bindInvoiceData(invoice: Invoice) {
        // 1. Đổ danh sách dịch vụ vào Adapter
        val listOrder = invoice.orderDetails ?: emptyList()
        serviceAdapter.submitList(listOrder)

        // 2. Cập nhật thông tin bàn và thời gian
        binding.tablenumber.text = "#${invoice.billiardTableId} - ${invoice.billiardTableName}"
        binding.timeplaying.text = if (!invoice.startTime.isNullOrEmpty()) {
            calculatePlayTime(invoice.startTime, invoice.endTime)
        } else {
            "0h 0p"
        }

        // 3. Xử lý tính toán tiền bạc (Dựa trên số liệu API đã lưu)
        val calculatedFB = listOrder.sumOf { it.price * it.quantity }.toDouble()
        val tienDoAn = if ((invoice.productAmount ?: 0.0) > 0) invoice.productAmount!! else calculatedFB

        val tienGio = invoice.serviceAmount ?: 0.0
        val taxAmount = invoice.taxAmount
        val subTotal = tienDoAn + tienGio
        val finalTotal = invoice.totalAmount

        // 4. Tính ra số tiền đã được giảm (Nếu có voucher lúc thanh toán)
        // Tiền giảm giá = (Tạm tính + Thuế) - Tổng tiền thực tế phải trả
        val expectedTotal = subTotal + taxAmount
        val discountAmount = if (expectedTotal > finalTotal) (expectedTotal - finalTotal) else 0.0

        // 5. Đổ dữ liệu lên UI
        binding.tvTotalTablePrice.text = formatCurrency(tienGio)
        binding.tvTotalFBPrice.text = formatCurrency(tienDoAn)
        binding.tvSubTotal.text = formatCurrency(subTotal)
        binding.tvVAT.text = formatCurrency(taxAmount)
        binding.tvDiscountValue.text = "- ${formatCurrency(discountAmount)}"
        binding.tvFinalTotal.text = formatCurrency(finalTotal)
    }

    // =========================================================
    // CÁC HÀM TIỆN ÍCH TIỂU CHUẨN
    // =========================================================

    private fun calculatePlayTime(startTimeString: String, endTimeString: String?): String {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val start = format.parse(startTimeString)?.time ?: return "0h 0p"

            // Nếu endTimeString null (đang chơi chưa xong) thì lấy thời gian hiện tại
            val end = if (!endTimeString.isNullOrEmpty()) {
                format.parse(endTimeString)?.time ?: System.currentTimeMillis()
            } else {
                System.currentTimeMillis()
            }

            var diff = end - start
            if (diff < 0) diff = 0

            val hours = TimeUnit.MILLISECONDS.toHours(diff)
            diff -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)

            "${hours}h ${minutes}p"
        } catch (e: Exception) {
            "0h 0p"
        }
    }

    private fun formatCurrency(amount: Double): String {
        return "%,d đ".format(amount.toLong()).replace(',', '.')
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LoadingUtils.hide() // Dọn dẹp loading nếu fragment bị hủy đột ngột
    }
}