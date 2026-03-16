package com.example.billiard.presentation.home.openingtable

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentBanDetailBinding
import com.example.billiard.presentation.adapter.OrderServiceAdapter
import com.example.billiard.domain.model.OrderServiceUiModel
import com.example.billiard.presentation.home.transfertable.TransferTableBottomSheet
import com.google.android.material.button.MaterialButton


class BanDetailFragment : BaseFragment<FragmentBanDetailBinding>(FragmentBanDetailBinding::inflate) {
    private lateinit var orderServiceAdapter: OrderServiceAdapter

    override fun setupViews() {
        setUpUI()
        setUpRecycle()
        createMockData()


    }

    private fun setUpUI(){
        binding.btnBack.setOnClickListener {
           findNavController().popBackStack()
        }
        binding.btnDongBan.setOnClickListener {
            handleDongBanClick()
        }
        binding.btnChuyenBan.setOnClickListener {
            val bottomSheet = TransferTableBottomSheet(
                currentTableName = "Bàn 5",
            ) { selectedTable ->
                // ĐOẠN CODE NÀY SẼ CHẠY KHI NGƯỜI DÙNG BẤM "XÁC NHẬN CHUYỂN"
                Toast.makeText(
                    requireContext(),
                    "Đang chuyển phiên làm việc sang ${selectedTable.name}...",
                    Toast.LENGTH_SHORT
                ).show()

                // viewModel.transferTable(oldTableId, selectedTable.id)
            }

            bottomSheet.show(childFragmentManager, "TransferTableBottomSheet")
        }
        binding.btnThanhToan.setOnClickListener {
            findNavController().navigate(R.id.action_banDetailFragment_to_paymentFragment)
        }
        binding.btnThemDichVu.setOnClickListener {
            findNavController().navigate(R.id.action_banDetailFragment_to_orderServiceFragment)
        }
    }
    private fun setUpRecycle(){
        orderServiceAdapter = OrderServiceAdapter()
        binding.rvOrderItems.apply {
            adapter = orderServiceAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        }

    }
    private fun handleDongBanClick() {
        val soPhutDaQua = 6

        if (soPhutDaQua > 5) {
            showWarningDialog()
        } else {
            showConfirmCloseDialog()
        }
    }

    private fun showWarningDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_warning_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()

        dialog.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnUnderstood = dialog.findViewById<MaterialButton>(R.id.btnUnderstood)
        btnUnderstood.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showConfirmCloseDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(
            width,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirmClose = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Chỉ tắt hộp thoại
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Đã đóng bàn thành công!", Toast.LENGTH_SHORT).show()
            // findNavController().popBackStack()
        }

        dialog.show()
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
        orderServiceAdapter.submitList(mockList)

        binding.tvItemCount.text = "${mockList.size} món"
        val tamTinh = mockList.sumOf { it.totalPrice }
        val tienGio = 110000

        val tongCong = tamTinh + tienGio
        binding.valTamTinh.text = formatCurrency(tamTinh)
        binding.valTienGio.text = formatCurrency(tienGio)
        binding.valTongCong.text = formatCurrency(tongCong)
    }
    private fun formatCurrency(amount: Int): String {
        return "%,dđ".format(amount).replace(',', '.')
    }

    override fun observeData() {

    }
}