package com.example.billiard.presentation.home.payment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentPaymentBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.Voucher
import com.example.billiard.presentation.adapter.OrderDetailAdapter
import com.example.billiard.presentation.payment.PaymentViewModel
import com.example.billiard.presentation.payment.VoucherBottomSheet
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class PaymentFragment : BaseFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate) {

    private val viewModel: PaymentViewModel by viewModels()
    private lateinit var serviceAdapter: OrderDetailAdapter

    private var invoiceId: Int = -1
    private var availableVouchers: List<Voucher> = emptyList()
    private var currentInvoice: Invoice? = null // THÊM MỚI: Biến lưu trữ hóa đơn hiện tại
    private var finalTotalCalculated: Double = 0.0 // THÊM MỚI: Lưu tổng tiền sau khi giảm giá
    private var subTotal: Double = 0.0 // Tạm tính (Tiền đồ ăn + Tiền giờ)
    private var taxAmount: Double = 0.0 // Thuế
    private var currentVoucher: Voucher? = null
    private var bankid: Int = 0

    override fun setupViews() {
        invoiceId = arguments?.getInt("INVOICE_ID") ?: -1

        if (invoiceId != -1) {
            viewModel.loadInvoiceDetail(invoiceId)
            viewModel.loadActiveVouchers()
        } else {
            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy Hóa đơn", Toast.LENGTH_SHORT).show()
        }
        viewModel.loadActiveBank()
        setupClick()
    }

    private fun setupClick() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        serviceAdapter = OrderDetailAdapter()
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

        binding.btnVoucherEmpty.setOnClickListener {
            if (availableVouchers.isEmpty()) {
                Toast.makeText(requireContext(), "Không có mã giảm giá nào", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bottomSheet = VoucherBottomSheet(
                voucherList = availableVouchers,
                currentlySelectedId = currentVoucher?.id
            ) { selectedVoucher ->
                applyVoucher(selectedVoucher)
            }
            bottomSheet.show(childFragmentManager, "VoucherSheet")
        }

        binding.btnRemoveVoucher.setOnClickListener { removeVoucher() }

        binding.btnConfirmPayment.setOnClickListener {
            // THÊM MỚI: Đóng gói Invoice và Final Total gửi sang màn kia
            currentInvoice?.let { invoice ->
                val bundle = Bundle().apply {
                    // Dùng Gson biến Object thành chuỗi String để truyền đi an toàn
                    putString("INVOICE_JSON", Gson().toJson(invoice))
                    putInt("BANK_ID", bankid)
                    Log.d("PaymentFragment", "Final Total: $finalTotalCalculated")
                    putFloat("FINAL_TOTAL", finalTotalCalculated.toFloat())
                    putLong("VOUCHER_ID", currentVoucher?.id ?: -1L) // Gửi kèm ID voucher (nếu có)
                }
                findNavController().navigate(R.id.action_paymentFragment_to_invoicePaymentFragment, bundle)
            } ?: run {
                Toast.makeText(requireContext(), "Dữ liệu hóa đơn chưa sẵn sàng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1. Lắng nghe API Hóa Đơn
                launch {
                    viewModel.invoiceState.collect { state ->
                        if (state is Resource.Success) {
                            val invoice = state.data
                            currentInvoice = invoice
                            val listOrder = invoice.orderDetails ?: emptyList()
                            serviceAdapter.submitList(listOrder)

                            // --- CẬP NHẬT GIAO DIỆN PHẦN THỜI GIAN ---
                            binding.tablenumber.text = "#${invoice.billiardTableId} - ${invoice.billiardTableName}"

                            if (!invoice.startTime.isNullOrEmpty()) {
                                binding.timeplaying.text = calculatePlayTime(invoice.startTime, invoice.endTime)
                            } else {
                                binding.timeplaying.text = "0h 0p"
                            }

                            // --- TÍNH TOÁN TIỀN BẠC ---
                            // Khắc phục lỗi API: Nếu productAmount bị 0, tự lôi danh sách món ra cộng
                            val calculatedFB = listOrder.sumOf { it.price * it.quantity }.toDouble()
                            val tienDoAn = if ((invoice.productAmount ?: 0.0) > 0) invoice.productAmount!! else calculatedFB

                            val tienGio = invoice.serviceAmount ?: 0.0
                            taxAmount = invoice.taxAmount
                            subTotal = tienDoAn + tienGio
                            val servicc = invoice.serviceAmount ?: 0.0
                            binding.tvTotalFBPrice.text = formatCurrency(tienDoAn+servicc)

                            // --- ĐỔ DỮ LIỆU LÊN GIAO DIỆN (Đúng 100% ID XML) ---
                            binding.tvTotalTablePrice.text = formatCurrency(tienGio)
                            binding.tvSubTotal.text = formatCurrency(subTotal)
                            binding.tvVAT.text = formatCurrency(taxAmount)

                            calculateFinalTotal()
                        }
                    }
                }

                // 2. Lắng nghe API Voucher
                launch {
                    viewModel.vouchersState.collect { state ->
                        if (state is Resource.Success) {
                            availableVouchers = state.data
                        }
                    }
                }
                launch {
                    viewModel.bankState.collect { state ->
                        if (state is Resource.Success) {
                            bankid = state.data.id
                        }
                    }
                }
            }
        }
    }

    private fun calculatePlayTime(startTimeString: String, endTimeString: String?): String {
        try {
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val start = format.parse(startTimeString)?.time ?: return "0h 0p"
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

            return "${hours}h ${minutes}p"
        } catch (e: Exception) {
            return "0h 0p"
        }
    }

    private fun applyVoucher(voucher: Voucher) {
        if (subTotal < voucher.minimumAmount) {
            val minAmountStr = formatCurrency(voucher.minimumAmount)
            Toast.makeText(requireContext(), "Đơn hàng phải từ $minAmountStr để áp dụng mã này!", Toast.LENGTH_LONG).show()
            return
        }

        currentVoucher = voucher
        binding.tvAppliedVoucherCode.text = voucher.code

        val isPercent = voucher.voucherType.equals("PERCENTAGE", ignoreCase = true)
        val descText = if (isPercent) "Giảm ${voucher.value.toInt()}%" else "Giảm ${formatCurrency(voucher.value)}"
        binding.tvAppliedVoucherDesc.text = descText

        binding.btnVoucherEmpty.visibility = View.GONE
        binding.layoutVoucherApplied.visibility = View.VISIBLE

        calculateFinalTotal()
        Toast.makeText(requireContext(), "Áp dụng mã thành công!", Toast.LENGTH_SHORT).show()
    }

    private fun removeVoucher() {
        currentVoucher = null
        binding.layoutVoucherApplied.visibility = View.GONE
        binding.btnVoucherEmpty.visibility = View.VISIBLE

        calculateFinalTotal()
        Toast.makeText(requireContext(), "Đã gỡ mã giảm giá", Toast.LENGTH_SHORT).show()
    }

    private fun calculateFinalTotal() {
        var discountAmount = 0.0

        currentVoucher?.let { voucher ->
            if (subTotal >= voucher.minimumAmount) {
                if (voucher.voucherType.equals("PERCENTAGE", ignoreCase = true) || voucher.voucherType.equals("PERCENT", ignoreCase = true)) {
                    discountAmount = subTotal * (voucher.value / 100.0)
                } else {
                    discountAmount = voucher.value
                }
            } else {
                removeVoucher()
                return
            }
        }

        if (discountAmount > subTotal) discountAmount = subTotal

        // Công thức chuẩn: Tổng = (Tạm tính + Thuế VAT) - Giảm giá
        val finalTotal = (subTotal + taxAmount) - discountAmount
        finalTotalCalculated = finalTotal // THÊM MỚI: Cập nhật biến toàn cục để truyền đi
        // --- CẬP NHẬT 2 TEXTVIEW CUỐI CÙNG ---
        // Sử dụng đúng ID tvDiscountValue bạn vừa thêm vào XML
        binding.tvDiscountValue.text = "- ${formatCurrency(discountAmount)}"
        binding.tvFinalTotal.text = formatCurrency(finalTotal)
    }

    private fun formatCurrency(amount: Double): String {
        return "%,d đ".format(amount.toLong()).replace(',', '.')
    }

}