package com.example.billiard.presentation.administrator.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePurchaseParam
import com.example.billiard.domain.model.PurchaseHistory
import com.example.billiard.domain.model.PurchaseInvoice
import com.example.billiard.domain.usecase.purchase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val createPurchaseInvoiceUseCase: CreatePurchaseInvoiceUseCase,
    private val getPurchaseInvoiceByIdUseCase: GetPurchaseInvoiceByIdUseCase,
    private val deletePurchaseInvoiceUseCase: DeletePurchaseInvoiceUseCase,
    private val searchPurchaseInvoicesUseCase: SearchPurchaseInvoicesUseCase
) : ViewModel() {

    // Danh sách hóa đơn nhập (Tab Lịch sử)
    private val _purchaseHistoriesState = MutableStateFlow<Resource<List<PurchaseHistory>>?>(null)
    val purchaseHistoriesState: StateFlow<Resource<List<PurchaseHistory>>?> = _purchaseHistoriesState.asStateFlow()

    // Chi tiết một hóa đơn (Khi click vào 1 item)
    private val _purchaseDetailState = MutableStateFlow<Resource<PurchaseInvoice>?>(null)
    val purchaseDetailState: StateFlow<Resource<PurchaseInvoice>?> = _purchaseDetailState.asStateFlow()

    // Trạng thái thêm/xóa
    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    // Theo yêu cầu của Backend, phải truyền 2 tham số date. Tạm thời mình truyền null để demo.
    // Tùy theo thiết kế, bạn sẽ lấy giá trị này từ DatePicker trên UI.
    fun searchPurchaseInvoices(startDate: String, endDate: String) {
        viewModelScope.launch {
            searchPurchaseInvoicesUseCase(startDate, endDate).collect { result ->
                _purchaseHistoriesState.value = result
            }
        }
    }

    fun createPurchaseInvoice(param: CreatePurchaseParam) {
        viewModelScope.launch {
            createPurchaseInvoiceUseCase(param).collect { result ->
                _actionState.value = result
                // Nếu tạo xong thành công thì chỉ cần báo UI Toast, UI sẽ back lại và load lại tab Lịch Sử
            }
        }
    }

    fun getPurchaseInvoiceById(id: Int) {
        viewModelScope.launch {
            getPurchaseInvoiceByIdUseCase(id).collect { result ->
                _purchaseDetailState.value = result
            }
        }
    }

    fun deletePurchaseInvoice(id: Int, currentStartDate: String, currentEndDate: String) {
        viewModelScope.launch {
            deletePurchaseInvoiceUseCase(id).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    // Load lại danh sách hóa đơn theo ngày đã chọn
                    searchPurchaseInvoices(currentStartDate, currentEndDate)
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}