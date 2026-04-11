package com.example.billiard.presentation.home.bottomopentable

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetOpenTableBinding
import com.example.billiard.domain.model.DashboardTable
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class OpenTableBottomSheet(
    private val ban: DashboardTable,
    private val onStartClicked: (DashboardTable) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetOpenTableBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetOpenTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
    }

    private fun setUpUI() {
        binding.tvTableName.text = ban.name
        // DashboardTable không chứa price, nếu cần lấy giá thì bạn phải truyền thêm hoặc ẩn đi
        binding.tvTableInfo.text = "Sảnh chính • ID bàn: ${ban.id}"

        binding.btnClose.setOnClickListener {
            dismiss() // Tắt bottom sheet
        }

        binding.btnStart.setOnClickListener {
            onStartClicked(ban)
            dismiss()
        }
        
        // Tạm thời truyền tên giả hoặc lấy type từ name
        val tmp = ban.name.split("-")
        val type = if (tmp.size > 1) tmp[1].trim() else "Bida Lỗ"
        updateTableTypeUI(type)
    }

    private fun updateTableTypeUI(selectedType: String) {
        val unselectedBg = R.drawable.bg_table_type_unselected
        val unselectedColor = Color.parseColor("#757575")

        binding.tvsnooker.setBackgroundResource(unselectedBg)
        binding.tvsnooker.setTextColor(unselectedColor)

        binding.tvspool.setBackgroundResource(unselectedBg)
        binding.tvspool.setTextColor(unselectedColor)

        binding.tvphang.setBackgroundResource(unselectedBg)
        binding.tvphang.setTextColor(unselectedColor)

        binding.tvvip.setBackgroundResource(unselectedBg)
        binding.tvvip.setTextColor(unselectedColor)

        val selectedBg = R.drawable.bg_table_type_selected
        val selectedColor = Color.parseColor("#2196F3")

        when (selectedType) {
            "Snooker" -> {
                binding.tvsnooker.setBackgroundResource(selectedBg)
                binding.tvsnooker.setTextColor(selectedColor)
            }
            "Bida Lỗ", "POOL" -> {
                binding.tvspool.setBackgroundResource(selectedBg)
                binding.tvspool.setTextColor(selectedColor)
            }
            "Phăng", "CAROM" -> {
                binding.tvphang.setBackgroundResource(selectedBg)
                binding.tvphang.setTextColor(selectedColor)
            }
            "VIP" -> {
                binding.tvvip.setBackgroundResource(selectedBg)
                binding.tvvip.setTextColor(selectedColor)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}