package com.example.billiard.presentation.administrator.inventory.receipt

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentReceiptDetailBinding
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.presentation.adapter.OrderServiceAdapter
import com.example.billiard.presentation.administrator.inventory.PurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReceiptDetailFragment : BaseFragment<FragmentReceiptDetailBinding>(FragmentReceiptDetailBinding::inflate) {

    private val viewModel: PurchaseViewModel by viewModels()
    private lateinit var detailAdapter: OrderServiceAdapter

    private val invoiceId: Int by lazy {
        arguments?.getInt("INVOICE_ID", -1) ?: -1
    }

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupRecyclerView()

        if (invoiceId != -1) {
            viewModel.getPurchaseInvoiceById(invoiceId)
        }
    }

    private fun setupRecyclerView() {
        detailAdapter = OrderServiceAdapter() // Adapter này đang dùng OrderServiceUiModel
        binding.rvProducts.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.purchaseDetailState.collect { state ->
                    when (state) {
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            val invoice = state.data
                            
                            // 1. Đổ thông tin chung của phiếu
                            binding.tvReceiptId.text = "PN${String.format("%03d", invoice.id)}"
                            binding.tvImporter.text = invoice.employeeName
                            
                            // Parse và format ngày giờ
                            binding.tvImportDate.text = try {
                                val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                val sdfOutput = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val date = sdfInput.parse(invoice.importDate)
                                date?.let { sdfOutput.format(it) } ?: invoice.importDate
                            } catch (e: Exception) {
                                invoice.importDate
                            }

                            // 2. Map dữ liệu sang OrderServiceUiModel để tái sử dụng Adapter cũ
                            val uiItems = invoice.details.map { detail ->
                                OrderServiceUiModel(
                                    orderId = detail.id.toString(),
                                    serviceId = detail.productId.toString(),
                                    name = detail.productName,
                                    category = "Đơn giá: %,dđ".format(detail.importPrice.toInt()).replace(',', '.'),
                                    imageUrl = detail.imageUrl,
                                    quantity = detail.quantity,
                                    unitPrice = detail.importPrice.toInt(),
                                    startTime = null, 
                                    endTime = null
                                )
                            }
                            detailAdapter.submitList(uiItems)

                            // 3. Tính tổng
                            val totalTypes = uiItems.size
                            val totalQuantity = uiItems.sumOf { it.quantity }

                            binding.tvTotalItemsCount.text = "$totalTypes mặt hàng ($totalQuantity SP)"
                            binding.tvFinalTotal.text = "%,dđ".format(invoice.totalAmount.toInt()).replace(',', '.')
                        }
                        is Resource.Error -> {
                            // Xử lý lỗi (Toast)
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}