package com.example.billiard.presentation.administrator.table

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentTableManageBinding
import com.example.billiard.domain.model.BanUiModel
import com.example.billiard.domain.model.TableCategoryUIModel
import com.example.billiard.domain.model.TableStatus
import com.example.billiard.presentation.adapter.BanListAdapter
import com.example.billiard.presentation.adapter.TableCategoryAdapter

class TableManagementFragment : BaseFragment<FragmentTableManageBinding>(FragmentTableManageBinding::inflate) {

    private lateinit var tableAdapter: BanListAdapter
    private lateinit var categoryAdapter: TableCategoryAdapter

    private var allTables = listOf<BanUiModel>()

    // Lưu trạng thái Lọc Kép
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
            Toast.makeText(requireContext(), "Mở màn hình Thêm Bàn Mới", Toast.LENGTH_SHORT).show()
        }

        // --- SỰ KIỆN TÌM KIẾM ---
        binding.btnSearch.setOnClickListener { showSearchBox() }

        binding.btnCloseSearch.setOnClickListener { hideSearchBox() }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchTablesByKeyword(text.toString())
        }
    }

    // --- HÀM ẨN/HIỆN GIAO DIỆN TÌM KIẾM ---
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
        binding.edtSearch.text.clear() // Xóa chữ sẽ tự động gọi lại doOnTextChanged để reset list

        binding.edtSearch.visibility = View.GONE
        binding.btnCloseSearch.visibility = View.GONE

        binding.tvHeaderTitle.visibility = View.VISIBLE
        binding.tvHeaderSub.visibility = View.VISIBLE
        binding.btnSearch.visibility = View.VISIBLE

        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
    }

    // --- LOGIC LỌC TỔNG HỢP ---
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

        // 1. Lọc theo Loại bàn (Nếu không phải "Tất cả")
        if (currentFilterName != "Tất cả") {
            filteredList = filteredList.filter { it.type == currentFilterName }
        }

        // 2. Lọc theo Tên bàn (Chứa ký tự đang gõ)
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { table ->
                table.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        tableAdapter.submitList(filteredList)

        // Cập nhật câu text hiển thị số lượng
        if (currentFilterName == "Tất cả" && currentSearchQuery.isEmpty()) {
            binding.tvHeaderSub.text = "Tổng cộng: ${filteredList.size} bàn"
        } else {
            binding.tvHeaderSub.text = "Tìm thấy ${filteredList.size} bàn"
        }
    }

    private fun setUpRecyclerViews() {
        categoryAdapter = TableCategoryAdapter { selectedCategory ->
            // Đổi gọi hàm ở đây
            filterTablesByCategory(selectedCategory.name)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            itemAnimator = null
        }

        tableAdapter = BanListAdapter(
            onEditClick = { selectedTable ->
                Toast.makeText(requireContext(), "Sửa bàn: ${selectedTable.name}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { selectedTable ->
                Toast.makeText(requireContext(), "Xóa bàn: ${selectedTable.name}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvTablelist.adapter = tableAdapter
    }

    private fun createMockData() {
        val categories = listOf(
            TableCategoryUIModel(id = "0", name = "Tất cả"),
            TableCategoryUIModel(id = "1", name = "Bida Lỗ"),
            TableCategoryUIModel(id = "2", name = "Snooker"),
            TableCategoryUIModel(id = "3", name = "VIP"),
            TableCategoryUIModel(id = "4", name = "Phăng")
        )
        categoryAdapter.submitList(categories)

        allTables = listOf(
            BanUiModel("1", "Bàn 01","80.000", "Bida Lỗ", TableStatus.PLAYING, "01:25:00"),
            BanUiModel("2", "Bàn 02", "80.000","Snooker", TableStatus.EMPTY),
            BanUiModel("3", "Bàn 03","80.000", "Bida Lỗ", TableStatus.PLAYING, "00:42:15"),
            BanUiModel("4", "Bàn 04", "80.000","VIP", TableStatus.EMPTY),
            BanUiModel("5", "Bàn 05", "80.000","Bida Lỗ", TableStatus.EMPTY),
            BanUiModel("6", "Bàn 06", "80.000","Snooker", TableStatus.MAINTAIN)
        )

        tableAdapter.submitList(allTables)
        binding.tvHeaderSub.text = "Tổng cộng: ${allTables.size} bàn"
    }

    override fun observeData() {}
}