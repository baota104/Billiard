package com.example.billiard.presentation.home.payment


import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentPaymentBinding
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.presentation.adapter.OrderServiceAdapter
import com.example.billiard.presentation.payment.VoucherBottomSheet


class PaymentFragment : BaseFragment<FragmentPaymentBinding>(FragmentPaymentBinding::inflate) {

    private lateinit var serviceAdapter: OrderServiceAdapter
    private var appliedVoucherId: String? = null
    override fun setupViews() {

        setupclick()
        createMockData()
    }
    private fun setupclick(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Setup Adapter Đồ ăn/uống
        serviceAdapter = OrderServiceAdapter()
        binding.rvServices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = serviceAdapter
        }

        // Setup các nút bấm
        binding.btnVoucherEmpty.setOnClickListener {
            val bottomSheet = VoucherBottomSheet(currentlySelectedId = appliedVoucherId) { selectedVoucher ->
                appliedVoucherId = selectedVoucher.id

                binding.tvAppliedVoucherCode.text = selectedVoucher.code
                binding.tvAppliedVoucherDesc.text = selectedVoucher.discountText

                binding.btnVoucherEmpty.visibility = View.GONE
                binding.layoutVoucherApplied.visibility = View.VISIBLE

                Toast.makeText(requireContext(), "Áp dụng thành công!", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(childFragmentManager, "VoucherSheet")
        }

        binding.btnRemoveVoucher.setOnClickListener {
            appliedVoucherId = null

            binding.layoutVoucherApplied.visibility = View.GONE
            binding.btnVoucherEmpty.visibility = View.VISIBLE

            Toast.makeText(requireContext(), "Đã gỡ mã giảm giá", Toast.LENGTH_SHORT).show()
        }

        binding.btnViewPDF.setOnClickListener {
            Toast.makeText(requireContext(), "Đang mở Hóa đơn PDF...", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloadPDF.setOnClickListener {
            Toast.makeText(requireContext(), "Đang tải xuống...", Toast.LENGTH_SHORT).show()
        }

        binding.btnConfirmPayment.setOnClickListener {
           findNavController().navigate(R.id.action_paymentFragment_to_invoicePaymentFragment)
        }
    }



    private fun createMockData() {
        val mockList = listOf(
            OrderServiceUiModel(
                orderId = "OD1", serviceId = "SV1", name = "Bia Heineken",
                category = "Đồ uống • 330ml", imageUrl = "", quantity = 2, unitPrice = 30000,
                startTime = null, endTime = null
            ),
            OrderServiceUiModel(
                orderId = "OD2", serviceId = "SV2", name = "Đậu phộng",
                category = "Đồ ăn nhẹ • Gói lớn", imageUrl = "", quantity = 1, unitPrice = 20000,
                startTime = null, endTime = null
            ),
            OrderServiceUiModel(
                orderId = "OD3", serviceId = "SV3", name = "Sting Dâu",
                category = "Nước ngọt", imageUrl = "", quantity = 2, unitPrice = 15000,
                startTime = null, endTime = null
            ),
            OrderServiceUiModel(
                orderId = "OD4", serviceId = "SV4", name = "Đĩa trái cây",
                category = "Khai vị • Theo mùa", imageUrl = "", quantity = 1, unitPrice = 80000,
                startTime = null, endTime = null
            )
        )
        serviceAdapter.submitList(mockList)
    }

    override fun observeData() {
        // Sau này ViewModel sẽ lấy tổng tiền từ DB và map vào các TextView:
        // binding.tvTotalTablePrice, binding.tvTotalFBPrice, binding.tvSubTotal...
    }}