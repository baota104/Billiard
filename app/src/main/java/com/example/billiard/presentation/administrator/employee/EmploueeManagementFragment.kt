package com.example.billiard.presentation.administrator.employee

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentEmploueeManagementBinding
import com.example.billiard.domain.model.Employee
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.presentation.adapter.EmployeeAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmployeeManagementFragment : BaseFragment<FragmentEmploueeManagementBinding>(FragmentEmploueeManagementBinding::inflate) {

    private lateinit var categoryAdapter: TableCategoryAdapter
    private lateinit var employeeAdapter: EmployeeAdapter
    private val viewModel: EmployeeManagementViewModel by viewModels()

    private var allEmployees = listOf<Employee>()

    private var currentFilterId = "ALL" // ALL, ACTIVE, INACTIVE
    private var currentSearchQuery = ""

    override fun setupViews() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.fabAddEmployee.setOnClickListener {
            val bottomSheet = ManageEmployeeBottomSheet(
                employeeToEdit = null,
                onSaveCreate = { param ->
                    viewModel.createEmployee(param)
                },
                onSaveUpdate = null
            )
            bottomSheet.show(childFragmentManager, "AddEmployee")
        }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchEmployees(text.toString())
        }

        setupRecyclerViews()
        setupCategoryTabs()
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
                val bottomSheet = ManageEmployeeBottomSheet(
                    employeeToEdit = emp,
                    onSaveCreate = null,
                    onSaveUpdate = { param ->
                        viewModel.updateEmployee(param)
                    }
                )
                bottomSheet.show(childFragmentManager, "EditEmployee")
            },
            onDeleteClick = { emp ->
                showConfirmCloseDialog(emp.id, emp.fullName)
            },
            onStatusChange = { emp, isActive ->
                // Gọi API update trạng thái (Chỉ đổi mỗi cờ isActive, giữ nguyên các thông tin khác)
                val param = com.example.billiard.domain.request.UpdateEmployeeParam(
                    id = emp.id,
                    firstName = emp.firstName,
                    lastName = emp.lastName,
                    email = emp.email,
                    phoneNumber = emp.phoneNumber,
                    role = emp.role,
                    isActive = isActive
                )
                viewModel.updateEmployee(param)
            }
        )

        binding.rvEmployees.adapter = employeeAdapter
    }

    private fun setupCategoryTabs() {
        val categories = listOf(
            TableCategoryUIModel(id = "ALL", name = "Tất cả"),
            TableCategoryUIModel(id = "ACTIVE", name = "Đang hoạt động"),
            TableCategoryUIModel(id = "INACTIVE", name = "Bị khóa")
        )
        categoryAdapter.submitList(categories)
    }

    private fun showConfirmCloseDialog(id: Int, name: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirmClose = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)
        btnConfirmClose.text = "Xóa nhân viên"
        
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.text = "Xác nhận xóa"
        
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)
        desc.text = "Bạn có chắc chắn muốn xóa nhân viên \"$name\"\nkhỏi hệ thống không? Hành động này không\nthể hoàn tác."

        btnCancel.setOnClickListener {
            dialog.dismiss() 
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteEmployee(id)
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

        // Lọc theo Tab (Hoạt động / Bị khóa)
        if (currentFilterId != "ALL") {
            val isSearchingActive = currentFilterId == "ACTIVE"
            filteredList = filteredList.filter { it.isActive == isSearchingActive }
        }

        // Lọc theo Text Search (Tên hoặc Email)
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { emp ->
                emp.fullName.contains(currentSearchQuery, ignoreCase = true) ||
                        emp.email.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        employeeAdapter.submitList(filteredList)
        
        // Cập nhật text số lượng ở Header
        if (currentSearchQuery.isEmpty() && currentFilterId == "ALL") {
            binding.tvTotalEmployee.text = "Tổng cộng: ${filteredList.size} nhân viên"
        } else {
            binding.tvTotalEmployee.text = "Tìm thấy ${filteredList.size} nhân viên"
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Lắng nghe dữ liệu danh sách nhân viên
                launch {
                    viewModel.employeesState.collect { resource ->
                        when (resource) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                val pageData = resource.data
                                allEmployees = pageData.content 
                                applyFilters() 
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            null -> {} 
                        }
                    }
                }

                // Lắng nghe kết quả thêm/sửa/xóa
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState()
                            }
                            null -> {}
                        }
                    }
                }
            }
        }
    }
}