package com.example.billiard.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.example.billiard.domain.usecase.invoice.GetInvoiceByIdUseCase
import com.example.billiard.domain.usecase.invoice.UpdateInvoiceUseCase
// import com.example.billiard.domain.usecase.orderdetail.CreateOrderDetailUseCase
// import com.example.billiard.domain.usecase.orderdetail.DeleteOrderDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BanDetailViewModel @Inject constructor(
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
) : ViewModel() {

    private val _invoiceState = MutableStateFlow<Resource<Invoice>>(Resource.Loading)
    val invoiceState: StateFlow<Resource<Invoice>> = _invoiceState.asStateFlow()

    // 2. STATE: Theo dõi quá trình Thanh toán / Cập nhật hóa đơn
    private val _checkoutState = MutableStateFlow<Resource<Invoice>?>(null)
    val checkoutState: StateFlow<Resource<Invoice>?> = _checkoutState.asStateFlow()


    fun loadInvoiceDetail(invoiceId: Int) {
        viewModelScope.launch {
            getInvoiceByIdUseCase(invoiceId).collect { result ->
                _invoiceState.value = result
            }
        }
    }

}