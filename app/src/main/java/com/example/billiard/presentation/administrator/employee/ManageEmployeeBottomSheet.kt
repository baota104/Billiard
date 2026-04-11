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
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.request.CreateEmployeeParam
import com.example.billiard.domain.request.UpdateEmployeeParam
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ManageEmployeeBottomSheet(
    private val employeeToEdit: Employee? = null,
    private val onSaveCreate: ((CreateEmployeeParam) -> Unit)? = null,
    private val onSaveUpdate: ((UpdateEmployeeParam) -> Unit)? = null
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetManageEmployeeBinding? = null
    private val binding get() = _binding!!

    // Mặc định vai trò là NHÂN VIÊN (EMPLOYEE)
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
            binding.tvTitle.text = "Thêm nhân viên mới"

            binding.lblStatusTitle.text = "Kích hoạt tài khoản"
            binding.lblStatusDesc.text = "Nhân viên có thể đăng nhập ngay sau khi tạo"
            binding.switchActive.isChecked = true

            binding.btnSave.text = "Lưu thông tin"

            setRoleUI(isAdmin = false)

            // Hiện các trường bổ sung khi tạo (Nếu layout có, ở đây mình giả định UI chỉ có name và username, 
            // ta sẽ phải tách ra thành firstName/lastName và dummy email để gửi Backend)

        } else {
            binding.tvTitle.text = "Chỉnh sửa nhân viên"

            // Do Backend trả về firstName và lastName riêng biệt, App hiển thị fullName
            binding.edtFullName.setText(employeeToEdit.fullName)
            binding.edtUsername.setText(employeeToEdit.email) // Giả định dùng ô Username để hiện/sửa email

            // Tên đăng nhập (Email/Username) không được phép sửa sau khi tạo
            binding.edtUsername.isEnabled = false

            binding.lblStatusTitle.text = "Trạng thái hoạt động"
            binding.lblStatusDesc.text = "Cho phép đăng nhập vào hệ thống"
            binding.switchActive.isChecked = employeeToEdit.isActive

            binding.btnSave.text = "Cập nhật"

            setRoleUI(isAdmin = employeeToEdit.role.equals("MANAGER", ignoreCase = true))
        }
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.cardRoleAdmin.setOnClickListener { setRoleUI(isAdmin = true) }
        binding.cardRoleEmployee.setOnClickListener { setRoleUI(isAdmin = false) }

        binding.btnSave.setOnClickListener {
            val fullNameInput = binding.edtFullName.text.toString().trim()
            val emailOrUsernameInput = binding.edtUsername.text.toString().trim()
            val role = if (isRoleAdmin) "MANAGER" else "EMPLOYEE"
            val isActive = binding.switchActive.isChecked

            if (fullNameInput.isEmpty() || emailOrUsernameInput.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tách FullName thành firstName và lastName
            val parts = fullNameInput.split(" ")
            val lastName = parts.first()
            val firstName = if (parts.size > 1) parts.drop(1).joinToString(" ") else ""

            if (employeeToEdit == null) {
                // TẠO MỚI
                val param = CreateEmployeeParam(
                    username = emailOrUsernameInput, // Tạm coi là Username để đăng nhập
                    password = "123", // Mặc định pass
                    firstName = firstName,
                    lastName = lastName,
                    email = "$emailOrUsernameInput@billiard.com", // Dummy email
                    phoneNumber = "0999999999", // Dummy phone
                    role = role
                )
                onSaveCreate?.invoke(param)
            } else {
                // CẬP NHẬT
                val param = UpdateEmployeeParam(
                    id = employeeToEdit.id,
                    firstName = firstName,
                    lastName = lastName,
                    email = employeeToEdit.email, // Giữ email cũ
                    phoneNumber = employeeToEdit.phoneNumber, // Giữ phone cũ
                    role = role,
                    isActive = isActive
                )
                onSaveUpdate?.invoke(param)
            }
            dismiss()
        }
    }

    private fun setRoleUI(isAdmin: Boolean) {
        isRoleAdmin = isAdmin
        val blueColor = ContextCompat.getColor(requireContext(), R.color.primary_blue)
        val grayColor = ContextCompat.getColor(requireContext(), R.color.text_gray)
        val transparent = Color.TRANSPARENT
        val white = Color.WHITE

        if (isAdmin) {
            binding.cardRoleAdmin.setCardBackgroundColor(blueColor)
            binding.tvRoleAdmin.setTextColor(white)

            binding.cardRoleEmployee.setCardBackgroundColor(transparent)
            binding.tvRoleEmployee.setTextColor(grayColor)
        } else {
            binding.cardRoleAdmin.setCardBackgroundColor(transparent)
            binding.tvRoleAdmin.setTextColor(grayColor)

            binding.cardRoleEmployee.setCardBackgroundColor(blueColor)
            binding.tvRoleEmployee.setTextColor(white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}