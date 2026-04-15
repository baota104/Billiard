package com.example.billiard.presentation.home.orderservice

import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billiard.core.base.BaseFragment
import com.example.billiard.core.network.Resource
import com.example.billiard.core.utils.LoadingUtils
import com.example.billiard.databinding.FragmentOrderServiceBinding
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.CategoryType
import com.example.billiard.domain.model.Product
import com.example.billiard.presentation.adapter.CategoryAdapter
import com.example.billiard.presentation.adapter.ServiceAdapter
import com.example.billiard.presentation.administrator.inventory.ProductViewModel
import com.example.billiard.presentation.home.bottomaddservice.AddServiceBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderServiceFragment : BaseFragment<FragmentOrderServiceBinding>(FragmentOrderServiceBinding::inflate) {

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var serviceAdapter: ServiceAdapter
    private val viewModel: ProductViewModel by viewModels()

    private var allCategories = listOf<Category>()
    private var allProducts = listOf<Product>()

    private var currentCategoryId: Int = -1
    private var currentSearchQuery: String = ""

    // 1. Khai báo biến chứa ID của Hóa đơn
    private var invoiceId: Int = -1

    override fun setupViews() {
        // 2. Lấy invoiceId từ màn hình Bàn truyền sang
        invoiceId = arguments?.getInt("INVOICE_ID") ?: -1

        setUpUI()
        setUpRecyclerViews()
    }

    private fun setUpUI() {
        binding.tvTitle.text = "Dịch vụ & Tiện ích"

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSearch.setOnClickListener { showSearchBox() }
        binding.btnCloseSearch.setOnClickListener { hideSearchBox() }

        binding.edtSearch.doOnTextChanged { text, _, _, _ ->
            searchServicesByKeyword(text.toString())
        }
    }

    private fun showSearchBox() {
        binding.tvTitle.visibility = View.GONE
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
        binding.tvTitle.visibility = View.VISIBLE
        binding.btnSearch.visibility = View.VISIBLE
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
    }

    private fun filterServicesByCategory(categoryId: Int) {
        currentCategoryId = categoryId
        applyFilters()
    }

    private fun searchServicesByKeyword(query: String) {
        currentSearchQuery = query.trim()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allProducts

        // ĐÃ FIX LỖI: Lọc theo danh mục
        if (currentCategoryId != -1) {
            // Cách 1: Nếu Product của bạn có trường categoryId thì dùng dòng này
            // filteredList = filteredList.filter { it.categoryId == currentCategoryId }

            // Cách 2: Nếu Product chỉ có categoryName (Dựa theo logic code cũ của bạn)
            val selectedCategoryName = allCategories.find { it.id == currentCategoryId }?.categoryName
            if (selectedCategoryName != null) {
                filteredList = filteredList.filter { it.categoryName == selectedCategoryName }
            }
        }

        // Lọc theo từ khóa tìm kiếm
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { product ->
                product.name.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        serviceAdapter.submitList(filteredList)
    }

    private fun setUpRecyclerViews() {
        categoryAdapter = CategoryAdapter { selectedCategory ->
            filterServicesByCategory(selectedCategory.id)
        }
        binding.rvCategories.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = null
        }

        serviceAdapter = ServiceAdapter { selectedProduct ->
            // Tìm đúng category của Product
            val productCategory = allCategories.find { it.categoryName == selectedProduct.categoryName }
            val categoryType = productCategory?.type ?: CategoryType.RETAIL

            val bottomSheet = AddServiceBottomSheet(selectedProduct, categoryType) { item, quantity ->
                // 3. GỌI API THÊM MÓN
                if (invoiceId != -1) {
                    viewModel.addServiceToInvoice(
                        invoiceId = invoiceId,
                        productId = item.id,
                        quantity = quantity,
                        price = item.sellingPrice
                    )
                } else {
                    Toast.makeText(requireContext(), "Lỗi: Không tìm thấy mã Hóa đơn", Toast.LENGTH_SHORT).show()
                }
            }
            bottomSheet.show(childFragmentManager, "AddService")
        }
        binding.rvServices.apply {
            adapter = serviceAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.productsState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                allProducts = state.data.content
                                applyFilters()
                            }
                            is Resource.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }
                }

                launch {
                    viewModel.categoriesState.collect { state ->
                        when (state) {
                            is Resource.Success -> {
                                val allCategory = Category(-1, "Tất cả", CategoryType.UNKNOWN, false)
                                val fullCategoryList = mutableListOf(allCategory)
                                fullCategoryList.addAll(state.data)
                                allCategories = fullCategoryList
                                categoryAdapter.submitList(fullCategoryList)
                            }
                            is Resource.Error -> Toast.makeText(requireContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show()
                            else -> {}
                        }
                    }
                }

                // 4. LẮNG NGHE KẾT QUẢ GỌI MÓN (ACTION STATE)
                launch {
                    viewModel.actionState.collect { state ->
                        when (state) {
                            is Resource.Loading -> {
                                LoadingUtils.show(requireContext())
                            }
                            is Resource.Success -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Thêm thành công!", Toast.LENGTH_SHORT).show()
                                viewModel.resetActionState() // Tránh toast lại khi xoay màn hình
                            }
                            is Resource.Error -> {
                                LoadingUtils.hide()
                                Toast.makeText(requireContext(), "Lỗi: ${state.message}", Toast.LENGTH_LONG).show()
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