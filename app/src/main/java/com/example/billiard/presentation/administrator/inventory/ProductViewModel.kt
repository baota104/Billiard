package com.example.billiard.presentation.administrator.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.model.PageData
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
import com.example.billiard.domain.usecase.category.GetCategoriesUseCase
import com.example.billiard.domain.usecase.product.DeleteProductUseCase
import com.example.billiard.domain.usecase.product.GetProductByIdUseCase
import com.example.billiard.domain.usecase.product.GetProductsUseCase
import com.example.billiard.domain.usecase.product.SearchProductsUseCase
import com.example.billiard.domain.usecase.product.UpsertProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val upsertProductUseCase: UpsertProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // Danh sách sản phẩm (Bây giờ dùng PageData thay cho List)
    private val _productsState = MutableStateFlow<Resource<PageData<Product>>?>(null)
    val productsState: StateFlow<Resource<PageData<Product>>?> = _productsState.asStateFlow()

    private val _productDetailState = MutableStateFlow<Resource<Product>?>(null)
    val productDetailState: StateFlow<Resource<Product>?> = _productDetailState.asStateFlow()

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>?>(null)
    val categoriesState: StateFlow<Resource<List<Category>>?> = _categoriesState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    // Phân trang
    private var currentPage = 0
    private var isLastPage = false
    var isLoadingMore = false

    private val currentProductList = mutableListOf<Product>()

    init {
        loadAllProducts()
        loadCategories()
    }

    fun loadAllProducts(isRefresh: Boolean = false) {
        if (isLoadingMore || (isLastPage && !isRefresh)) return

        if (isRefresh) {
            currentPage = 0
            isLastPage = false
            currentProductList.clear()
            _productsState.value = Resource.Loading
        }

        isLoadingMore = true

        viewModelScope.launch {
            getProductsUseCase(page = currentPage, size = 20).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        currentProductList.addAll(result.data.content)
                        isLastPage = result.data.isLastPage
                        if (!isLastPage) currentPage++

                        // Phát danh sách cộng dồn cho UI
                        val mergedPageData = PageData(
                            content = currentProductList.toList(),
                            totalElements = result.data.totalElements,
                            totalPages = result.data.totalPages,
                            currentPage = result.data.currentPage,
                            isLastPage = result.data.isLastPage
                        )
                        _productsState.value = Resource.Success(mergedPageData)
                        isLoadingMore = false
                    }
                    is Resource.Error -> {
                        _productsState.value = Resource.Error(result.message)
                        isLoadingMore = false
                    }
                    is Resource.Loading -> {
                        if (isRefresh) _productsState.value = Resource.Loading
                    }
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                _categoriesState.value = result
            }
        }
    }

    fun searchProducts(keyword: String) {
        if (keyword.isBlank()) {
            loadAllProducts(isRefresh = true)
            return
        }
        viewModelScope.launch {
            // Giữ nguyên Flow của Search, nhưng map sang PageData ảo để cùng giao tiếp 1 kiểu state
            searchProductsUseCase(keyword).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val dummyPageData = PageData(
                            content = result.data,
                            totalElements = result.data.size,
                            totalPages = 1,
                            currentPage = 0,
                            isLastPage = true
                        )
                        _productsState.value = Resource.Success(dummyPageData)
                    }
                    is Resource.Error -> _productsState.value = Resource.Error(result.message)
                    is Resource.Loading -> _productsState.value = Resource.Loading
                }
            }
        }
    }

    fun getProductById(productId: Int) {
        viewModelScope.launch {
            getProductByIdUseCase(productId).collect { result ->
                _productDetailState.value = result
            }
        }
    }

    fun upsertProduct(param: UpsertProductParam) {
        viewModelScope.launch {
            upsertProductUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadAllProducts(isRefresh = true) // Cập nhật lại UI sau khi thêm/sửa
                }
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            deleteProductUseCase(productId).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadAllProducts(isRefresh = true) // Cập nhật lại UI sau khi xóa
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}