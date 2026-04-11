package com.example.billiard.presentation.administrator.table

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.billiard.R
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.databinding.FragmentTableManageBinding
import com.example.billiard.domain.model.CreateTableParam
import com.example.billiard.domain.model.DashboardTable
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.domain.model.UpdateTableParam
import com.example.billiard.presentation.adapter.BanListAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TableManagementFragment : BaseFragment<FragmentTableManageBinding>(FragmentTableManageBinding::inflate) {

    private val viewModel: TableManageViewModel by viewModels()

    private lateinit var tableAdapter: BanListAdapter
    private lateinit var categoryAdapter: TableCategoryAdapter

    private var allTables = listOf<DashboardTable>()

    private var currentFilterName = "Tất cả"
    private var currentSearchQuery = ""

    override fun setupViews() {
        setUpUI()
        setUpRecyclerViews()
        createMockData()
    }

    private fun setUpUI() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.fabAddTable.setOnClickListener {
            val bottomSheet = ManageTableBottomSheet(tableToEdit = null) { name, type, isMaintain, imageFile ->
                val param = CreateTableParam(
                    name = name,
                    status = if (isMaintain) "MAINTENANCE" else "AVAILABLE",
                    tableType = type,
                    imageFile = imageFile 
                )
                viewModel.createTable(param)
            }
            bottomSheet.show(childFragmentManager, "AddTable")
        }

        binding.btnSearch.setOnClickListener { showSearchBox() }
        binding.btnCloseSearch.setOnClickListener { hideSearchBox() }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchTablesByKeyword(text.toString())
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.dashboardTablesState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                allTables = state.data.content
                                applyFilters()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                            null -> {}
                        }
                    }
                }

                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> { }
                            is Resource.Success -> {
                                Toast.makeText(requireContext(), "Thao tác thành công!", Toast.LENGTH_SHORT).show()
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

    private fun showSearchBox() {
        binding.tvHeaderTitle.visibility = View.GONE
        binding.tvHeaderSub.visibility = View.GONE
        binding.btnSearch.visibility = View.GONE

        binding.edtSearch.visibility = View.VISIBLE
        binding.btnCloseSearch.visibility = View.VISIBLE

        binding.edtSearch.requestFocus()
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideSearchBox() {
        binding.edtSearch.text.clear()

        binding.edtSearch.visibility = View.GONE
        binding.btnCloseSearch.visibility = View.GONE

        binding.tvHeaderTitle.visibility = View.VISIBLE
        binding.tvHeaderSub.visibility = View.VISIBLE
        binding.btnSearch.visibility = View.VISIBLE

        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
    }

    private fun filterTablesByCategory(filterName: String) {
        currentFilterName = filterName
        applyFilters()
    }

    private fun searchTablesByKeyword(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allTables

        if (currentFilterName != "Tất cả") {
            filteredList = filteredList.filter { table -> 
                table.tableType.equals(currentFilterName, ignoreCase = true)
            }
        }

        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { table ->
                table.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        tableAdapter.submitList(filteredList)

        if (currentFilterName == "Tất cả" && currentSearchQuery.isEmpty()) {
            binding.tvHeaderSub.text = "Tổng cộng: ${filteredList.size} bàn"
        } else {
            binding.tvHeaderSub.text = "Tìm thấy ${filteredList.size} bàn"
        }
    }

    private fun setUpRecyclerViews() {
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            filterTablesByCategory(selectedCategory.name)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        tableAdapter = BanListAdapter(
            onEditClick = { selectedTable ->
                val bottomSheet = ManageTableBottomSheet(tableToEdit = selectedTable) { name, type, isMaintain, imageFile ->
                    val param = UpdateTableParam(
                        id = selectedTable.id,
                        name = name,
                        tableType = type,
                        status = if (isMaintain) "MAINTENANCE" else "AVAILABLE",
                        imageFile = imageFile
                    )
                    viewModel.updateTable(param)
                }
                bottomSheet.show(childFragmentManager, "EditTable")
            },
            onDeleteClick = { selectedTable ->
                showConfirmCloseDialog(selectedTable)
            }
        )
        
        binding.rvTablelist.apply {
            adapter = tableAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    }
                }
            })
        }
    }

    private fun createMockData() {
        val categories = listOf(
            TableCategoryUIModel(id = "0", name = "Tất cả"),
            TableCategoryUIModel(id = "1", name = "POOL"),
            TableCategoryUIModel(id = "2", name = "SNOOKER"),
            TableCategoryUIModel(id = "3", name = "CAROM"),
        )
        categoryAdapter.submitList(categories)
    }

    private fun showConfirmCloseDialog(table: DashboardTable) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm_close_table)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btnConfirmClose = dialog.findViewById<MaterialButton>(R.id.btnConfirmClose)
        btnConfirmClose.text = "Xác nhận xóa"
        
        val iconthongbao = dialog.findViewById<ImageView>(R.id.iconthongbao)
        iconthongbao.setImageResource(R.drawable.ic_priority)
        
        val title = dialog.findViewById<TextView>(R.id.tvTitle)
        title.text = "Xác nhận xóa bàn"
        
        val desc = dialog.findViewById<TextView>(R.id.tvMessage)
        desc.text = "Bạn có chắc chắn muốn xóa \"${table.name}\"\nkhỏi hệ thống không? Hành động này không\nthể hoàn tác."

        btnCancel.setOnClickListener {
            dialog.dismiss() 
        }

        btnConfirmClose.setOnClickListener {
            dialog.dismiss()
            viewModel.deleteTable(table.id)
        }

        dialog.show()
    }
}