package com.example.billiard.presentation.administrator.employee

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentEmploueeManagementBinding
import com.example.billiard.domain.model.EmployeeUiModel
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.presentation.adapter.EmployeeAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.google.android.material.button.MaterialButton

class EmployeeManagementFragment : BaseFragment<FragmentEmploueeManagementBinding>(FragmentEmploueeManagementBinding::inflate) {

    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var employeeAdapter: EmployeeAdapter

    private var allEmployees = listOf<EmployeeUiModel>()

    private var currentFilterId = "ALL" // ALL, ACTIVE, INACTIVE
    private var currentSearchQuery = ""

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddEmployee.setOnClickListener {
            val bottomSheet = ManageEmployeeBottomSheet(employeeToEdit = null) { name, username, role, isActive ->
                Toast.makeText(requireContext(), "Đã thêm $name ($role)", Toast.LENGTH_SHORT).show()
            }
            bottomSheet.show(childFragmentManager, "AddEmployee")
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchEmployees(text.toString())
        }

        setupRecyclerViews()
        createMockData()
    }

    private fun setupRecyclerViews() {
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            filterByCategory(selectedCategory.id)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        employeeAdapter = EmployeeAdapter(
            onEditClick = { emp ->
                val bottomSheet = ManageEmployeeBottomSheet(employeeToEdit = emp) { name, username, role, isActive ->
                    Toast.makeText(requireContext(), "Đã cập nhật $name ($role)", Toast.LENGTH_SHORT).show()
                }
                bottomSheet.show(childFragmentManager, "EditEmployee") },
            onDeleteClick = { emp ->
                showConfirmCloseDialog(emp.name)
            },
            onStatusChange = { emp, isActive ->
                val statusStr = if (isActive) "Mở khóa" else "Khóa"
                Toast.makeText(requireContext(), "Đã $statusStr tài khoản ${emp.name}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rvEmployees.adapter = employeeAdapter
    }
    private fun showConfirmCloseDialog(name:String) {
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
        btnConfirmClose.setText("Xóa nhân viên")
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.setText("Xác nhận xóa ")
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)

        desc.setText("Bạn có chắc chắn muốn xóa nhân viên \"$name\"\n" +
                "khỏi hệ thống không? Hành động này không\n" +
                "thể hoàn tác.")

        btnCancel.setOnClickListener {
            dialog.dismiss() // Chỉ tắt hộp thoại
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(requireContext(), "Đã đóng xóa bàn thành công!", Toast.LENGTH_SHORT).show()
            // findNavController().popBackStack()
        }

        dialog.show()
    }

    private fun filterByCategory(categoryId: String) {
        currentFilterId = categoryId
        applyFilters()
    }

    private fun searchEmployees(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allEmployees

        if (currentFilterId != "ALL") {
            val isSearchingActive = currentFilterId == "ACTIVE"
            filteredList = filteredList.filter { it.isActive == isSearchingActive }
        }

        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { emp ->
                emp.name.contains(currentSearchQuery, ignoreCase = true) ||
                        emp.username.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        employeeAdapter.submitList(filteredList)
    }

    private fun createMockData() {
        allEmployees = listOf(
            EmployeeUiModel("1", "Trần Hà Linh", "@user01", "NHÂN VIÊN", "15/10/2023", avatarUrl = "has_image", initials = null, isActive = true),
            EmployeeUiModel("2", "Trần Thị Hoa", "@hoa.tran", "NHÂN VIÊN", "20/10/2023", avatarUrl = null, initials = "TH", isActive = true),
            EmployeeUiModel("3", "Lê Văn Minh", "@minh.le", "NHÂN VIÊN", "01/11/2023", avatarUrl = null, initials = "LM", isActive = false)
        )

        // Cập nhật số lượng trên Header
        binding.tvTotalEmployee.text = "${allEmployees.size} nhân viên"

        // Setup Tabs
        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả"),
            TableCategoryUIModel(id = "ACTIVE", name = "Đang hoạt động"),
            TableCategoryUIModel(id = "INACTIVE", name = "Bị khóa")
        )
        categoryAdapter.submitList(categories)

        employeeAdapter.submitList(allEmployees)
    }

    override fun observeData() {}
}