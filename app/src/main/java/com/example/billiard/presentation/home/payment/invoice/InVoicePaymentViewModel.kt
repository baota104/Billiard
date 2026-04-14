package com.example.billiard.presentation.home.payment.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.example.billiard.domain.model.VietQr
import com.example.billiard.domain.usecase.bank.GetVietQrUseCase
import com.example.billiard.domain.usecase.invoice.UpdateInvoiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InVoicePaymentViewModel @Inject constructor(
    private val updateInvoiceStatusUseCase: UpdateInvoiceUseCase,
    private val getVietQrUseCase: GetVietQrUseCase
) : ViewModel() {

    // Trạng thái load ảnh QR (giả định UseCase trả về chuỗi String là URL)
    private val _qrState = MutableStateFlow<Resource<VietQr>?>(null)
    val qrState: StateFlow<Resource<VietQr>?> = _qrState.asStateFlow()

    // Trạng thái cập nhật hóa đơn
    private val _paymentState = MutableStateFlow<Resource<Invoice>?>(null)
    val paymentState: StateFlow<Resource<Invoice>?> = _paymentState.asStateFlow()

    fun loadQrCode(bankId: Int) {
        viewModelScope.launch {
            _qrState.value = Resource.Loading
            // Truyền duy nhất bankId theo đúng yêu cầu API
            getVietQrUseCase(bankId).collect { result ->
                _qrState.value = result
            }
        }
    }

    fun completePayment(param: UpdateInvoiceParam) {
        viewModelScope.launch {
            _paymentState.value = Resource.Loading
            updateInvoiceStatusUseCase(param).collect { result ->
                _paymentState.value = result
            }
        }
    }
}