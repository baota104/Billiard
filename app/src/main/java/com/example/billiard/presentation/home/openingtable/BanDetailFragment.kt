package com.example.billiard.presentation.home.openingtable

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
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
import com.example.billiard.databinding.FragmentBanDetailBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.presentation.adapter.OrderDetailAdapter
import com.example.billiard.presentation.detail.BanDetailViewModel
import com.example.billiard.presentation.home.transfertable.TransferTableBottomSheet
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BanDetailFragment : BaseFragment<FragmentBanDetailBinding>(FragmentBanDetailBinding::inflate) {
    private lateinit var orderServiceAdapter: OrderDetailAdapter
    private val viewModel: BanDetailViewModel by viewModels()
    private var invoiceId: Int = -1
    private var timerJob: Job? = null
    private lateinit var invoice: Invoice
    override fun onResume() {
        super.onResume()
        // Mỗi khi màn hình này hiện lên (kể cả từ màn hình Gọi món quay về)
        // -> Tự động gọi API lấy dữ liệu mới nhất
        if (invoiceId != 0 && invoiceId != -1) {
            Log.d("BanDetail_BUG", "Đang tự động Refresh lại dữ liệu của Invoice: $invoiceId")
            viewModel.loadInvoiceDetail(invoiceId)
        }
    }
    override fun setupViews() {
        invoiceId = arguments?.getInt("INVOICE_ID") ?: 0
//        Log.d("BanDetail_BUG", "1. Mở màn hình với Invoice ID: $invoiceId")
//
//        if (invoiceId != 0) {
//            viewModel.loadInvoiceDetail(invoiceId)
//        } else {
//            Toast.makeText(requireContext(), "Lỗi: Không tìm thấy mã Hóa đơn", Toast.LENGTH_SHORT).show()
//        }
        setUpUI()
        setUpRecycle()
    }

    private fun setUpUI() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnDongBan.setOnClickListener { showConfirmCloseDialog() }

        binding.btnChuyenBan.setOnClickListener {
            val bottomSheet = TransferTableBottomSheet(currentTableName = "Bàn 5") { selectedTable ->
                Toast.makeText(requireContext(), "Đang chuyển sang ${selectedTable.name}...", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(childFragmentManager, "TransferTableBottomSheet")
        }

        binding.btnThanhToan.setOnClickListener {
            val bundle = Bundle().apply { putInt("INVOICE_ID", invoiceId) }
            findNavController().navigate(R.id.action_banDetailFragment_to_paymentFragment,bundle)
        }

        binding.btnThemDichVu.setOnClickListener {
            val bundle = Bundle().apply { putInt("INVOICE_ID", invoiceId) }
            findNavController().navigate(R.id.action_banDetailFragment_to_orderServiceFragment, bundle)
        }
    }

    private fun setUpRecycle() {
        orderServiceAdapter = OrderDetailAdapter()
        binding.rvOrderItems.apply {
            adapter = orderServiceAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.invoiceState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            Log.d("BanDetail_BUG", "2. API đang Loading...")
                        }
                        is Resource.Success -> {
                            val invoice = state.data
                            Log.d("BanDetail_BUG", "3. API Success! Lấy được hóa đơn ID: ${invoice.id}")
                            Log.d("BanDetail_BUG", "   - Thời gian bắt đầu: ${invoice.startTime}")

                            // 1. Cập nhật danh sách món ăn
                            val listOrder = invoice.orderDetails ?: emptyList()
                            orderServiceAdapter.submitList(listOrder)
                            Log.d("BanDetail_BUG", "   - Danh sách món ăn: $listOrder")
                            binding.tvItemCount.text = "${listOrder.size} món"

                            // 2. Vì Backend tự tính tiền, bạn lấy thẳng từ Model Invoice gán vào
                            // TODO: Cập nhật lại các trường này cho đúng với Model Invoice của bạn
                            // Ví dụ: val tamTinh = invoice.productAmount, val tienGio = invoice.serviceAmount...
                            val tamTinh = listOrder.sumOf { (it.price * it.quantity).toInt() } // Giữ tạm logic này nếu BE chưa trả total món
                            val tienGio = 110000 // Tạm fix cứng hoặc lấy từ invoice.tienGio

                            binding.valTamTinh.text = formatCurrency(tamTinh)

                            // 3. Kích hoạt đếm giờ
                            if (!invoice.startTime.isNullOrEmpty()) {
                                Log.d("BanDetail_BUG", "4. Chuẩn bị gọi hàm startTimer()")
                                startTimer(invoice.startTime)
                            } else {
                                Log.e("BanDetail_BUG", "LỖI: invoice.startTime đang bị null hoặc rỗng!")
                            }
                        }
                        is Resource.Error -> {
                            Log.e("BanDetail_BUG", "LỖI API: ${state.message}")
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun startTimer(startTimeString: String) {
        timerJob?.cancel()
        Log.d("BanDetail_BUG", "5. Bắt đầu xử lý timer với chuỗi: $startTimeString")

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            // Cấu hình format. NẾU BACKEND TRẢ VỀ KHÁC ĐỊNH DẠNG NÀY SẼ BỊ CRASH PARSE!
            val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            //serverFormat.timeZone = TimeZone.getTimeZone("UTC")

            val startTimeDate = try {
                serverFormat.parse(startTimeString)
            } catch (e: Exception) {
                Log.e("BanDetail_BUG", "6. CRASH! Không thể Parse chuỗi thời gian: ${e.message}")
                null
            }

            if (startTimeDate == null) {
                Log.e("BanDetail_BUG", "7. Hủy bỏ đếm giờ do Parse thất bại.")
                return@launch // Thoát luôn coroutine
            }

            Log.d("BanDetail_BUG", "8. Parse thành công! Thời gian chuẩn: $startTimeDate")
            val startTimeMillis = startTimeDate.time

            val displayFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.tvTimeInfo.text = "Bắt đầu: ${displayFormat.format(startTimeDate)}"

            while (isActive) {
                val currentTimeMillis = System.currentTimeMillis()
                var diff = currentTimeMillis - startTimeMillis

                if (diff < 0) diff = 0

                val days = TimeUnit.MILLISECONDS.toDays(diff)
                diff -= TimeUnit.DAYS.toMillis(days)

                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                diff -= TimeUnit.HOURS.toMillis(hours)

                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                diff -= TimeUnit.MINUTES.toMillis(minutes)

                val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)

                // Cập nhật giao diện đồng hồ
                binding.tvday.text = days.toString()
                binding.tvhour.text = String.format("%02d", hours)
                binding.tvminute.text = String.format("%02d", minutes)
                binding.tvsecond.text = String.format("%02d", seconds)

                // Log kiểm tra xem vòng lặp có đang chạy không (Log mỗi 10 giây để đỡ spam)
                if (seconds % 10L == 0L) {
                    Log.d("BanDetail_BUG", "9. Timer đang chạy: $hours giờ $minutes phút $seconds giây")
                }

                delay(1000)
            }
        }
    }

    private fun formatCurrency(amount: Int): String {
        return "%,dđ".format(amount).replace(',', '.')
    }

    // Các hàm dialog giữ nguyên...
    private fun showWarningDialog() { /* ... */ }
    private fun showConfirmCloseDialog() { /* ... */ }
}