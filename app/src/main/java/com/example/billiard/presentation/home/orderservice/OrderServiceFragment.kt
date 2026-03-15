package com.example.billiard.presentation.home.orderservice

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.databinding.FragmentOrderServiceBinding
import com.example.billiard.domain.model.CategoryUiModel
import com.example.billiard.domain.model.ServiceItemUiModel
import com.example.billiard.presentation.adapter.CategoryAdapter
import com.example.billiard.presentation.adapter.ServiceAdapter
import com.example.billiard.presentation.home.bottomaddservice.AddServiceBottomSheet

class OrderServiceFragment : BaseFragment<FragmentOrderServiceBinding>(FragmentOrderServiceBinding::inflate) {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var serviceAdapter: ServiceAdapter

    private var allServices = listOf<ServiceItemUiModel>()

    // Lưu trạng thái Lọc hiện tại
    private var currentCategoryId: String = "all"
    private var currentSearchQuery: String = ""

    override fun setupViews() {
        setUpUI()
        setUpRecyclerViews()
        createMockData()
    }

    private fun setUpUI() {
        binding.tvTitle.text = "Dịch vụ & Tiện ích Bàn 5"

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 1. Khi bấm Kính Lúp -> Bật ô Tìm kiếm
        binding.btnSearch.setOnClickListener {
            showSearchBox()
        }

        // 2. Khi bấm Dấu X -> Tắt ô Tìm kiếm
        binding.btnCloseSearch.setOnClickListener {
            hideSearchBox()
        }

        // 3. Lắng nghe từng ký tự được gõ vào ô EditText
        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchServicesByKeyword(text.toString())
        }
    }

    // --- HÀM ẨN HIỆN UI TÌM KIẾM ---
    private fun showSearchBox() {
        binding.tvTitle.visibility = View.GONE
        binding.btnSearch.visibility = View.GONE

        binding.edtSearch.visibility = View.VISIBLE
        binding.btnCloseSearch.visibility = View.VISIBLE

        // Tự động focus con trỏ chuột vào ô nhập liệu
        binding.edtSearch.requestFocus()

        // Bật bàn phím ảo lên
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.showSoftInput(binding.edtSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideSearchBox() {
        // Xóa sạch chữ trong ô tìm kiếm (Sẽ tự động trigger doOnTextChanged để reset list)
        binding.edtSearch.text.clear()

        binding.edtSearch.visibility = View.GONE
        binding.btnCloseSearch.visibility = View.GONE

        binding.tvTitle.visibility = View.VISIBLE
        binding.btnSearch.visibility = View.VISIBLE

        // Ẩn bàn phím ảo đi
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
    }

    // --- LOGIC LỌC DỮ LIỆU TỔNG HỢP ---
    private fun filterServicesByCategory(categoryId: String) {
        currentCategoryId = categoryId
        applyFilters()
    }

    private fun searchServicesByKeyword(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allServices

        // Lọc theo Category
        if (currentCategoryId != "all") {
            filteredList = filteredList.filter { it.categoryId == currentCategoryId }
        }

        // Lọc theo chữ gõ vào (Không phân biệt hoa/thường)
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { service ->
                service.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        serviceAdapter.submitList(filteredList)
    }

    // --- SETUP RECYCLE VÀ DỮ LIỆU ---
    private fun setUpRecyclerViews() {
        categoryAdapter = CategoryAdapter { selectedCategory ->
            filterServicesByCategory(selectedCategory.id)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = null
        }

        serviceAdapter = ServiceAdapter { selectedService ->
            val bottomSheet = AddServiceBottomSheet(selectedService) { item, quantity ->
                // Hành động khi user bấm "Xác nhận thêm"
                Toast.makeText(
                    requireContext(),
                    "Đã thêm $quantity x ${item.name} vào bàn!",
                    Toast.LENGTH_SHORT
                ).show()

                // TODO: Gọi ViewModel lưu xuống bảng OrderDichVu trong DB
            }
            bottomSheet.show(childFragmentManager, "AddService")
        }
        binding.rvServices.apply {
            adapter = serviceAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun createMockData() {
        val mockCategories = listOf(
            CategoryUiModel(id = "all", name = "Tất cả"),
            CategoryUiModel(id = "c1", name = "Thuê Gậy"),
            CategoryUiModel(id = "c2", name = "Đồ uống"),
            CategoryUiModel(id = "c3", name = "Đồ ăn nhẹ")
        )

        allServices = listOf(
            ServiceItemUiModel(id = "s1", name = "Bò húc", price = 20000, imageUrl = "", categoryId = "c2"),
            ServiceItemUiModel(id = "s2", name = "Coca Cola", price = 15000, imageUrl = "", categoryId = "c2"),
            ServiceItemUiModel(id = "s3", name = "Nước suối", price = 10000, imageUrl = "", categoryId = "c2"),
            ServiceItemUiModel(id = "s4", name = "Mì tôm", price = 12000, imageUrl = "", categoryId = "c3"),
            ServiceItemUiModel(id = "s5", name = "Đậu phộng", price = 10000, imageUrl = "", categoryId = "c3"),
            ServiceItemUiModel(id = "s6", name = "Khoai tây chiên", price = 25000, imageUrl = "", categoryId = "c3"),
            ServiceItemUiModel(id = "s7", name = "Thuê gậy thi đấu", price = 50000, imageUrl = "", categoryId = "c1", isRental = true)

        )

        categoryAdapter.submitList(mockCategories)
        serviceAdapter.submitList(allServices)
    }

    override fun observeData() {}
}