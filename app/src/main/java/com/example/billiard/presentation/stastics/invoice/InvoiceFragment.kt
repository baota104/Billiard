package com.example.billiard.presentation.stastics.invoice

import com.example.billiard.presentation.adapter.InvoiceAdapter
import com.example.billiard.presentation.invoice.InvoiceViewModel

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
import com.example.billiard.databinding.FragmentInvoiceBinding
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

    override fun setupViews() {
        // Nút Back
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Cài đặt RecyclerView
        invoiceAdapter = InvoiceAdapter { selectedInvoice ->
            // Khi bấm vào 1 dòng hóa đơn
            Toast.makeText(requireContext(), "Đã chọn hóa đơn ID: ${selectedInvoice.id}", Toast.LENGTH_SHORT).show()
            // TODO: Chuyển sang màn chi tiết hóa đơn (nếu có)
            // val action = InvoiceFragmentDirections.actionToDetail(selectedInvoice.id)
            // findNavController().navigate(action)
        }

        binding.rvInvoices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = invoiceAdapter
        }

        // Cài đặt lịch chọn ngày
        setupDateRangePicker()

        // Sự kiện khi bấm nút Thống kê
        binding.btnFilter.setOnClickListener {
            val startDateStr = binding.edtStartDate.text.toString()
            val endDateStr = binding.edtEndDate.text.toString()

            if (startDateStr.isEmpty() || endDateStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn khoảng thời gian", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

        }
    }

    private fun setupDateRangePicker() {
        // Khởi tạo Material Date Range Picker
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Chọn khoảng thời gian thống kê")
            .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
            .build()

        // Lắng nghe sự kiện người dùng bấm "Save/Lưu" trên bảng lịch
        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDateMillis = selection.first
            val endDateMillis = selection.second

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Gán chuỗi ngày đã format vào 2 ô EditText
            binding.edtStartDate.setText(sdf.format(Date(startDateMillis)))
            binding.edtEndDate.setText(sdf.format(Date(endDateMillis)))
        }

        // Tạo hành động chung: Bấm vào 1 trong 2 ô đều mở lên chung 1 bảng lịch
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

                        }
                        is Resource.Success -> {
                            // Gửi danh sách dữ liệu vào Adapter
                            invoiceAdapter.submitList(state.data)
                        }
                        is Resource.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}