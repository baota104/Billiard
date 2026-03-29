package com.example.billiard.presentation.administrator.inventory.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentReceiptDetailBinding
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.presentation.adapter.OrderServiceAdapter

class ReceiptDetailFragment : BaseFragment<FragmentReceiptDetailBinding>(FragmentReceiptDetailBinding::inflate) {

    // SỬ DỤNG ADAPTER DÙNG CHUNG
    private lateinit var detailAdapter: OrderServiceAdapter

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        setupRecyclerView()
        loadMockData()
    }

    private fun setupRecyclerView() {
        detailAdapter = OrderServiceAdapter()
        binding.rvProducts.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun loadMockData() {
        // 1. Đổ thông tin chung của phiếu
        binding.tvReceiptId.text = "PN-20231024-001"
        binding.tvImporter.text = "Nguyễn Văn An"
        binding.tvImportDate.text = "24/10/2023 14:30"

        // 2. Tái sử dụng OrderServiceUiModel để đổ danh sách
        val items = listOf(
            OrderServiceUiModel(
                orderId = "1", serviceId = "sp1",
                name = "Bia Heineken (Lon)",
                category = "Đơn giá: 18.000đ", // Mượn trường category làm text mô tả ở dưới tên món
                imageUrl = "",
                quantity = 24, unitPrice = 18000,
                startTime = null, endTime = null
            ),
            OrderServiceUiModel(
                orderId = "2", serviceId = "sp2",
                name = "Nước suối Aquafina 500ml",
                category = "Đơn giá: 6.000đ",
                imageUrl = "",
                quantity = 48, unitPrice = 6000,
                startTime = null, endTime = null
            ),
            OrderServiceUiModel(
                orderId = "3", serviceId = "sp3",
                name = "Bao tay vải cao cấp",
                category = "Đơn giá: 25.000đ",
                imageUrl = "",
                quantity = 10, unitPrice = 25000,
                startTime = null, endTime = null
            )
        )
        detailAdapter.submitList(items)

        // 3. Tận dụng thuộc tính totalPrice đã có sẵn trong Model để tính tổng cuối cùng
        val totalTypes = items.size
        val totalQuantity = items.sumOf { it.quantity }
        val finalPrice = items.sumOf { it.totalPrice } // Gọi it.totalPrice tiện hơn rất nhiều!

        binding.tvTotalItemsCount.text = "$totalTypes mặt hàng ($totalQuantity SP)"
        binding.tvFinalTotal.text = "%,dđ".format(finalPrice).replace(',', '.')
    }

    override fun observeData() {}
}