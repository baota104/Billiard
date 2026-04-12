package com.example.billiard.presentation.administrator.inventory.receipt

import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentCreateReceiptBinding
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseDetail
import com.example.billiard.domain.model.PurchaseDetailParam
import com.example.billiard.presentation.adapter.ReceiptItemAdapter
import com.example.billiard.presentation.administrator.inventory.PurchaseViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class CreateReceiptFragment : BaseFragment<FragmentCreateReceiptBinding>(FragmentCreateReceiptBinding::inflate) {

    private val viewModel: PurchaseViewModel by viewModels()

    private lateinit var receiptAdapter: ReceiptItemAdapter
    private var currentReceiptItems = mutableListOf<PurchaseDetail>()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.tvImportDate.text = sdf.format(Date())

        setupRecyclerView()
        setupActionButtons()

        // Lắng nghe dữ liệu trả về từ màn AddExistingProductFragment hoặc CreateNewProductFragment
        setFragmentResultListener("ADD_PRODUCT_REQUEST") { _, bundle ->
            val productId = bundle.getInt("productId")
            val productName = bundle.getString("productName", "")
            val quantity = bundle.getInt("quantity")
            val importPrice = bundle.getDouble("importPrice")
            val imageUrl = bundle.getString("imageUrl", "") // Lấy ảnh
            
            val subTotal = quantity * importPrice

            val existingItem = currentReceiptItems.find { it.productId == productId }
            if (existingItem != null) {
                val newIndex = currentReceiptItems.indexOf(existingItem)
                val newQuantity = existingItem.quantity + quantity
                val newSubTotal = newQuantity * existingItem.importPrice

                val updatedItem = existingItem.copy(
                    quantity = newQuantity,
                    subTotal = newSubTotal
                )
                currentReceiptItems[newIndex] = updatedItem
            } else {
                currentReceiptItems.add(
                    PurchaseDetail(
                        id = 0,
                        productId = productId,
                        productName = productName,
                        quantity = quantity,
                        importPrice = importPrice,
                        subTotal = subTotal,
                        imageUrl = imageUrl // Đã bổ sung trường ảo này
                    )
                )
            }
            updateListAndTotal()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Lắng nghe trạng thái tạo hóa đơn nhập (PurchaseInvoice)
                viewModel.actionState.collect { state ->
                    when (state) {
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            showToast("Lưu phiếu nhập thành công!")
                            viewModel.resetActionState()
                            findNavController().popBackStack()
                        }
                        is Resource.Error -> {
                            showToast("Lỗi lưu phiếu: ${state.message}")
                            viewModel.resetActionState()
                        }
                        null -> {}
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        receiptAdapter = ReceiptItemAdapter { itemToDelete ->
            showConfirmDialog(
                title = "Xóa mặt hàng",
                message = "Bạn muốn xóa ${itemToDelete.productName} khỏi phiếu nhập?",
                confirmButtonText = "Xóa"
            ) {
                currentReceiptItems.remove(itemToDelete)
                updateListAndTotal()
            }
        }
        binding.rvReceiptItems.adapter = receiptAdapter
    }

    private fun setupActionButtons() {
        binding.btnAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_createReceiptFragment_to_addExistingProductFragment)
        }

        binding.btnSaveReceipt.setOnClickListener {
            if (currentReceiptItems.isEmpty()) {
                showToast("Phiếu nhập đang trống!")
                return@setOnClickListener
            }

            // Dù list UI có chứa ImageUrl ảo đi chăng nữa, khi gửi xuống DB chỉ cần bóc tách các trường Backend cần
            val details = currentReceiptItems.map { item ->
                PurchaseDetailParam(
                    productId = item.productId,
                    quantity = item.quantity,
                    importPrice = item.importPrice
                )
            }
            
            val param = CreatePurchaseParam(details = details)
            viewModel.createPurchaseInvoice(param)
        }
    }

    private fun updateListAndTotal() {
        receiptAdapter.submitList(currentReceiptItems.toList())
        val total = currentReceiptItems.sumOf { it.subTotal }
        binding.tvTotalReceiptPrice.text = "%,dđ".format(total.toInt()).replace(',', '.')
    }
}