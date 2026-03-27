package com.example.billiard.presentation.administrator.table

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.example.billiard.R
import com.example.billiard.core.ext.hide
import com.example.billiard.core.ext.show
import com.example.billiard.databinding.BottomSheetManageTableBinding
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableStatus
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class ManageTableBottomSheet(
    private val tableToEdit: BanUiModel? = null, // null = Chế độ THÊM, có data = Chế độ SỬA
    private val onSave: (name: String, type: String, isMaintain: Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageTableBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.setBackgroundColor(Color.TRANSPARENT)
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetManageTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        setupDropdown()
        setupUIByMode()
        setupClickListeners()
    }

    private fun setupDropdown() {
        val types = arrayOf("Bida Lỗ", "Snooker", "VIP", "Phăng")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, types)
        binding.actCategory.setAdapter(adapter)
    }

    private fun setupUIByMode() {
        if (tableToEdit == null) {
            // ================== CHẾ ĐỘ THÊM BÀN MỚI ==================
            binding.tvTitle.text = "Thêm bàn mới"
            binding.tvAsterisk.show()

            // Ẩn/Hiện các nút
            binding.btnClose.hide()
            binding.cardMaintenance.hide()

            binding.btnSave.text = "Lưu thông tin"

            // Mặc định chọn loại bàn đầu tiên
            binding.actCategory.setText("Bida Lỗ", false)

        } else {
            // ================== CHẾ ĐỘ SỬA THÔNG TIN ==================
            binding.tvTitle.text = "Chỉnh sửa thông tin"
            binding.tvAsterisk.hide()

            // Ẩn/Hiện các nút
            binding.btnClose.show()
            binding.cardMaintenance.show()

            binding.btnSave.text = "Lưu thay đổi"

            // Điền sẵn dữ liệu cũ vào form
            binding.edtTableName.setText(tableToEdit.name)
            binding.actCategory.setText(tableToEdit.type, false)

            // Bật Switch nếu trạng thái đang là Bảo Trì
            binding.switchMaintenance.isChecked = tableToEdit.status == TableStatus.MAINTAIN

            // Giả lập giao diện đã có ảnh
            binding.layoutEmptyImage.hide()
            binding.layoutFilledImage.show()
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }

        // Click tải ảnh lên
        binding.layoutImageUpload.setOnClickListener {
            Toast.makeText(requireContext(), "Mở thư viện ảnh của điện thoại...", Toast.LENGTH_SHORT).show()
        }

        // Nút Lưu
        binding.btnSave.setOnClickListener {
            val name = binding.edtTableName.text.toString().trim()
            val type = binding.actCategory.text.toString()
            val isMaintain = binding.switchMaintenance.isChecked

            if (name.isEmpty()) {
                binding.edtTableName.error = "Vui lòng nhập tên bàn"
                return@setOnClickListener
            }
            onSave(name, type, isMaintain)
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}