package com.example.billiard.presentation.home.transfertable

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetTransferTableBinding
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableStatus
import com.example.billiard.presentation.adapter.TransferTableAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TransferTableBottomSheet(
    private val currentTableName: String,
    private val onConfirmTransfer: (BanUiModel) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetTransferTableBinding? = null
    private val binding get() = _binding!!
    private var availableTables: List<BanUiModel> = listOf()

    // Biến lưu trữ bàn đang được người dùng bấm chọn
    private var selectedTable: BanUiModel? = null

    // Ép style trong suốt cho BottomSheet để hiển thị góc bo tròn của file XML
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                // Ép luôn luôn mở bung toàn bộ
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTransferTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMockData()
        setUpUI()
        setUprecycle()



    }
    private fun setUprecycle(){
        val adapter = TransferTableAdapter { table ->
            selectedTable = table

            // Bật sáng nút "Xác nhận chuyển"
            binding.btnConfirmTransfer.isEnabled = true
            binding.btnConfirmTransfer.alpha = 1.0f // Làm cho nút đậm màu lên
        }

        binding.rvAvailableTables.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvAvailableTables.adapter = adapter
        adapter.submitList(availableTables)


    }
    private fun setUpUI(){
        binding.tvTitle.text = "Chuyển $currentTableName sang..."
        binding.btnClose.setOnClickListener {
            dismiss()
        }
        //  Mặc định làm mờ nút Xác nhận khi chưa chọn bàn nào
        binding.btnConfirmTransfer.isEnabled = false
        binding.btnConfirmTransfer.alpha = 0.5f

        // 5. Xử lý Nút Xác nhận chuyển
        binding.btnConfirmTransfer.setOnClickListener {
            selectedTable?.let { table ->
                onConfirmTransfer(table) // Báo về cho Fragment
                dismiss() // Đóng Bottom Sheet
            }
        }

    }
    private fun createMockData() {
        availableTables = listOf(
            BanUiModel("2", "Bàn 02", "80.000","Snooker", TableStatus.EMPTY),
            BanUiModel("4", "Bàn 04", "80.000","VIP", TableStatus.EMPTY),
            BanUiModel("5", "Bàn 05", "80.000","Bida Lỗ", TableStatus.EMPTY),
            BanUiModel("6", "Bàn 05", "80.000","Bida Lỗ", TableStatus.EMPTY),
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}