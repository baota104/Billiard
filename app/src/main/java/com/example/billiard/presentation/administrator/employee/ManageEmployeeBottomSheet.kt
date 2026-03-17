package com.example.billiard.presentation.administrator.employee

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.billiard.R
import com.example.billiard.databinding.BottomSheetManageEmployeeBinding
import com.example.billiard.domain.model.EmployeeUiModel
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManageEmployeeBottomSheet(
    private val employeeToEdit: EmployeeUiModel? = null,
    private val onSave: (name: String, username: String, role: String, isActive: Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageEmployeeBinding? = null
    private val binding get() = _binding!!

    // Mặc định vai trò là NHÂN VIÊN
    private var isRoleAdmin = false

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
        _binding = BottomSheetManageEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (view.parent as? View)?.setBackgroundColor(Color.TRANSPARENT)

        setupUIByMode()
        setupClickListeners()
    }

    private fun setupUIByMode() {
        if (employeeToEdit == null) {
            // ================== CHẾ ĐỘ THÊM MỚI ==================
            binding.tvTitle.text = "Thêm nhân viên mới"

            // Xử lý Trạng thái
            binding.lblStatusTitle.text = "Kích hoạt tài khoản"
            binding.lblStatusDesc.text = "Nhân viên có thể đăng nhập ngay sau khi tạo"
            binding.switchActive.isChecked = true

            // Nút Lưu
            binding.btnSave.text = "Lưu thông tin"

            // Mặc định Chọn Nhân viên
            setRoleUI(isAdmin = false)

        } else {
            // ================== CHẾ ĐỘ SỬA ==================
            binding.tvTitle.text = "Chỉnh sửa nhân viên"

            // Đổ dữ liệu cũ vào form
            binding.edtFullName.setText(employeeToEdit.name)
            binding.edtUsername.setText(employeeToEdit.username)
            // Tên đăng nhập thường không được phép sửa sau khi tạo
            binding.edtUsername.isEnabled = false

            binding.lblStatusTitle.text = "Trạng thái hoạt động"
            binding.lblStatusDesc.text = "Cho phép đăng nhập vào hệ thống"
            binding.switchActive.isChecked = employeeToEdit.isActive

            // Nút Lưu
            binding.btnSave.text = "Cập nhật"

            // Gán vai trò
            setRoleUI(isAdmin = employeeToEdit.role.uppercase().contains("QUẢN LÝ"))
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }


        // Click chọn vai trò Quản lý
        binding.cardRoleAdmin.setOnClickListener { setRoleUI(isAdmin = true) }

        // Click chọn vai trò Nhân viên
        binding.cardRoleEmployee.setOnClickListener { setRoleUI(isAdmin = false) }

        // Click Lưu
        binding.btnSave.setOnClickListener {
            val name = binding.edtFullName.text.toString().trim()
            val username = binding.edtUsername.text.toString().trim()
            val role = if (isRoleAdmin) "QUẢN LÝ" else "NHÂN VIÊN"
            val isActive = binding.switchActive.isChecked

            if (name.isEmpty() || username.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onSave(name, username, role, isActive)
            dismiss()
        }
    }

    // Hàm chuyển đổi màu sắc cho 2 nút Vai trò
    private fun setRoleUI(isAdmin: Boolean) {
        isRoleAdmin = isAdmin
        val blueColor = ContextCompat.getColor(requireContext(), R.color.primary_blue)
        val grayColor = ContextCompat.getColor(requireContext(), R.color.text_gray)
        val transparent = Color.TRANSPARENT
        val white = Color.WHITE

        if (isAdmin) {
            // Quản lý sáng lên
            binding.cardRoleAdmin.setCardBackgroundColor(blueColor)
            binding.tvRoleAdmin.setTextColor(white)

            // Nhân viên xám đi
            binding.cardRoleEmployee.setCardBackgroundColor(transparent)
            binding.tvRoleEmployee.setTextColor(grayColor)
        } else {
            // Quản lý xám đi
            binding.cardRoleAdmin.setCardBackgroundColor(transparent)
            binding.tvRoleAdmin.setTextColor(grayColor)

            // Nhân viên sáng lên
            binding.cardRoleEmployee.setCardBackgroundColor(blueColor)
            binding.tvRoleEmployee.setTextColor(white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}