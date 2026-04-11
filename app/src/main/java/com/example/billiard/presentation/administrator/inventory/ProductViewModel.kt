package com.example.billiard.presentation.administrator.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Product
import com.example.billiard.domain.request.UpsertProductParam
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
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    // Danh sách sản phẩm
    private val _productsState = MutableStateFlow<Resource<List<Product>>?>(null)
    val productsState: StateFlow<Resource<List<Product>>?> = _productsState.asStateFlow()

    // Chi tiết 1 sản phẩm
    private val _productDetailState = MutableStateFlow<Resource<Product>?>(null)
    val productDetailState: StateFlow<Resource<Product>?> = _productDetailState.asStateFlow()

    // Trạng thái các action: Thêm/Sửa/Xóa
    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    init {
        loadAllProducts()
    }

    fun loadAllProducts() {
        viewModelScope.launch {
            getProductsUseCase().collect { result ->
                _productsState.value = result
            }
        }
    }

    fun searchProducts(keyword: String) {
        if (keyword.isBlank()) {
            loadAllProducts()
            return
        }
        viewModelScope.launch {
            searchProductsUseCase(keyword).collect { result ->
                _productsState.value = result
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
                    loadAllProducts() // Reload lại danh sách sau khi thêm/sửa thành công
                }
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            deleteProductUseCase(productId).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadAllProducts() // Reload lại danh sách sau khi xóa thành công
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}