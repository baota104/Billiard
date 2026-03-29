package com.example.billiard.presentation.administrator.bank

import androidx.navigation.fragment.findNavController
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.ext.showConfirmDialog
import com.example.billiard.core.ext.showToast
import com.example.billiard.databinding.FragmentBankManagementBinding
import com.example.billiard.domain.model.BankUiModel
import com.example.billiard.presentation.adapter.BankAdapter

class BankFragment : BaseFragment<FragmentBankManagementBinding>(FragmentBankManagementBinding::inflate) {

    private lateinit var bankAdapter: BankAdapter

    // Lưu danh sách hiện tại để dễ thao tác
    private var currentBankList = mutableListOf<BankUiModel>()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAddBank.setOnClickListener {
            val bottomSheet = AddBankBottomSheet()
            bottomSheet.onBankAdded = { bankName, accNum, accHolder ->
                showToast("Đã thêm: $bankName")
            }
            bottomSheet.show(childFragmentManager, "AddBankSheet")
        }

        setupRecyclerView()
        loadMockData() // Giả lập dữ liệu từ DB
    }

    private fun setupRecyclerView() {
        bankAdapter = BankAdapter(
            onSetDefaultClick = { clickedBank ->
                // LOGIC CHỌN MẶC ĐỊNH: Chỉ có 1 thẻ được làm mặc định
                val updatedList = currentBankList.map { bankItem ->
                    // Copy lại object, nếu đúng ID vừa click thì set isDefault = true, còn lại = false
                    bankItem.copy(isDefault = bankItem.id == clickedBank.id)
                }.toMutableList()

                currentBankList = updatedList
                bankAdapter.submitList(currentBankList)
                showToast("Đã thiết lập ${clickedBank.bankName} làm mặc định")

                // TODO: Gọi API hoặc Update Database field 'bank_status' = 1 cho ngân hàng này
            },
            onDeleteClick = { bankToDelete ->
                showConfirmDialog(
                    title = "Xóa tài khoản",
                    message = "Bạn có chắc chắn muốn xóa tài khoản ${bankToDelete.bankName} không?",
                    confirmButtonText = "Xóa"
                ) {
                    val updatedList = currentBankList.filter { it.id != bankToDelete.id }.toMutableList()
                    currentBankList = updatedList
                    bankAdapter.submitList(currentBankList)
                    showToast("Đã xóa tài khoản")

                }
            }
        )

        binding.rvBankAccounts.adapter = bankAdapter
    }

    private fun loadMockData() {
        // Dữ liệu giả lập khớp với ảnh thiết kế
        currentBankList = mutableListOf(
            BankUiModel(1, "Vietcombank", "NGUYEN VAN A", "888812345678", isDefault = true),
            BankUiModel(2, "Techcombank", "NGUYEN VAN A", "190345678901", isDefault = false),
            BankUiModel(3, "VP Bank", "NGUYEN VAN A", "224488990011", isDefault = false)
        )
        bankAdapter.submitList(currentBankList)
    }

    override fun observeData() {
        // Sau này dùng ViewModel:
        // viewModel.bankList.observe(viewLifecycleOwner) { list ->
        //     currentBankList = list.toMutableList()
        //     bankAdapter.submitList(list)
        // }
    }
}