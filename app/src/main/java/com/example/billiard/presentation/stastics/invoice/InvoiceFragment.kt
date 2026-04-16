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
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentInvoiceBinding
import com.example.billiard.domain.model.Invoice
import com.example.billiard.presentation.adapter.InvoiceAdapter
import com.example.billiard.presentation.invoice.InvoiceViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class InvoiceFragment : BaseFragment<FragmentInvoiceBinding>(FragmentInvoiceBinding::inflate) {

    private val viewModel: InvoiceViewModel by viewModels()
    private lateinit var invoiceAdapter: InvoiceAdapter

    // Biến lưu trữ TOÀN BỘ hóa đơn tải từ API để lọc local
    private var allInvoices: List<Invoice> = emptyList()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        invoiceAdapter = InvoiceAdapter { selectedInvoice ->
            // Thêm dòng if này để chặn lỗi double-click gây crash
            if (findNavController().currentDestination?.id == R.id.invoiceFragment) {

                val bundle = Bundle().apply {
                    putInt("INVOICE_ID", selectedInvoice.id)
                }
                findNavController().navigate(R.id.action_invoiceFragment_to_invoiceDetailFragment, bundle)

                Toast.makeText(requireContext(), "Đã chọn hóa đơn ID: ${selectedInvoice.id}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvInvoices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = invoiceAdapter
        }

        setupDateRangePicker()

        // 1. Gọi API tải toàn bộ hóa đơn khi mới vào màn hình
        viewModel.loadInvoices()

        // 2. Xử lý Lọc Local khi bấm Thống kê
        binding.btnFilter.setOnClickListener {
            val startDateStr = binding.edtStartDate.text.toString()
            val endDateStr = binding.edtEndDate.text.toString()

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            filterInvoicesLocally(startDateStr, endDateStr)
        }
    }

    private fun filterInvoicesLocally(startDateStr: String, endDateStr: String) {
        try {
            val filterFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            // Lấy timestamp bắt đầu (00:00:00 của ngày bắt đầu)
            val startTimeMillis = filterFormat.parse(startDateStr)?.time ?: 0L

            // Lấy timestamp kết thúc (Cộng thêm 1 ngày trừ đi 1 mili-giây để bao trọn tới 23:59:59 của ngày kết thúc)
            val endTimeMillis = filterFormat.parse(endDateStr)?.let { it.time + 86399999L } ?: Long.MAX_VALUE

            // Lọc danh sách Local
            val filteredList = allInvoices.filter { invoice ->
                if (invoice.startTime.isNullOrEmpty()) return@filter false

                try {
                    val invoiceTime = serverFormat.parse(invoice.startTime)?.time ?: 0L
                    invoiceTime in startTimeMillis..endTimeMillis
                } catch (e: Exception) {
                    false
                }
            }

            // Gửi danh sách đã lọc lên giao diện
            invoiceAdapter.submitList(filteredList)

            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "Không có hóa đơn nào trong khoảng thời gian này", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Lỗi định dạng ngày tháng", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Chọn khoảng thời gian thống kê")
            .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDateMillis = selection.first
            val endDateMillis = selection.second

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.edtStartDate.setText(sdf.format(Date(startDateMillis)))
            binding.edtEndDate.setText(sdf.format(Date(endDateMillis)))

            // (Tuỳ chọn) Bạn có thể bỏ comment dòng dưới nếu muốn nó tự động lọc ngay khi chọn lịch xong
            // filterInvoicesLocally(binding.edtStartDate.text.toString(), binding.edtEndDate.text.toString())
        }

        val showPickerAction = View.OnClickListener {
            if (!dateRangePicker.isAdded) {
                dateRangePicker.show(childFragmentManager, "DATE_RANGE_PICKER")
            }
        }

        binding.edtStartDate.setOnClickListener(showPickerAction)
        binding.edtEndDate.setOnClickListener(showPickerAction)
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.invoicesState.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            LoadingUtils.show(requireContext())
                        }
                        is Resource.Success -> {
                            // Lưu data vào biến Local
                            LoadingUtils.hide()
                            allInvoices = state.data ?: emptyList()
                            // Hiển thị toàn bộ lên Adapter
                            invoiceAdapter.submitList(allInvoices)
                        }
                        is Resource.Error -> {
                            LoadingUtils.hide()
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}