package com.example.billiard.presentation.administrator.bank

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentBankManagementBinding
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.presentation.adapter.BankAdapter
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BankFragment : BaseFragment<FragmentBankManagementBinding>(FragmentBankManagementBinding::inflate) {

    private val viewModel: BankViewModel by viewModels()

    private lateinit var bankAdapter: BankAdapter
    private var currentBankList = listOf<Bank>()

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAddBank.setOnClickListener {
            val bottomSheet = AddBankBottomSheet { param ->
                // Gọi ViewModel tạo tài khoản
                viewModel.createBank(param)
            }
            bottomSheet.show(childFragmentManager, "AddBankSheet")
        }

        setupRecyclerView()
        // viewModel.loadBanks() tự chạy trong init{} rồi
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 1. Lắng nghe danh sách Ngân hàng
                launch {
                    viewModel.banksState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                LoadingUtils.show(requireContext())
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                currentBankList = state.data
                                bankAdapter.submitList(currentBankList)
                            }
                            is Resource.Error -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Lỗi tải dữ liệu ngân hàng: ${state.message}", Toast.LENGTH_SHORT).show()
                            }
                            null -> {}
                        }
                    }
                }

                // 2. Lắng nghe thông báo CRUD (Thêm, Sửa Mặc định, Xóa)
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                LoadingUtils.show(requireContext())
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Thao tác thành công!", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            is Resource.Error -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Lỗi thao tác: ${state.message}", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        bankAdapter = BankAdapter(
            onSetDefaultClick = { clickedBank ->
                // Do một ứng dụng chỉ được 1 bank làm mặc định để show QRCode, 
                // Khi User ấn "Chọn Mặc định", ta phải PUT lên API với bankStatus = true (isActive)
                // Các bank cũ nếu Backend làm chuẩn thì Backend sẽ tự gỡ status các bank kia về false
                val updateParam = UpdateBankParam(
                    id = clickedBank.id,
                    bankBin = clickedBank.bankBin,
                    bankAccountNo = clickedBank.bankAccountNo,
                    bankAccountName = clickedBank.bankAccountName,
                    bankStatus = true, // Kích hoạt 
                    employeeId = 0 // Tùy nghiệp vụ bạn lấy từ SessionManager nếu cần
                )
                viewModel.updateBank(updateParam)
            },
            onDeleteClick = { bankToDelete ->
                showConfirmDeleteDialog(bankToDelete)
            }
        )

        binding.rvBankAccounts.adapter = bankAdapter
    }

    private fun showConfirmDeleteDialog(bank: Bank) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirmClose = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)
        btnConfirmClose.text = "Xóa tài khoản"
        
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.text = "Xác nhận xóa tài khoản"
        
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)
        desc.text = "Bạn có chắc chắn muốn xóa tài khoản\n${bank.bankName} - ${bank.bankAccountNo}\nkhỏi hệ thống không?"

        btnCancel.setOnClickListener {
            dialog.dismiss() 
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteBank(bank.id)
        }

        dialog.show()
    }
}