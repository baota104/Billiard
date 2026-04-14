package com.example.billiard.presentation.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.Voucher
import com.example.billiard.domain.usecase.bank.getActiveUseCase
import com.example.billiard.domain.usecase.invoice.GetInvoiceByIdUseCase
import com.example.billiard.domain.usecase.voucher.GetActiveVouchersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
    private val getActiveVouchersUseCase: GetActiveVouchersUseCase, // Tiêm UseCase thật
    private val getactiveusecase: getActiveUseCase
) : ViewModel() {

    private val _invoiceState = MutableStateFlow<Resource<Invoice>>(Resource.Loading)
    val invoiceState: StateFlow<Resource<Invoice>> = _invoiceState.asStateFlow()

    private val _vouchersState = MutableStateFlow<Resource<List<Voucher>>>(Resource.Loading)
    val vouchersState: StateFlow<Resource<List<Voucher>>> = _vouchersState.asStateFlow()

    private val _bankState = MutableStateFlow<Resource<Bank>>(Resource.Loading)
    val bankState: StateFlow<Resource<Bank>> = _bankState.asStateFlow()


    fun loadActiveBank() {
        viewModelScope.launch {
            getactiveusecase().collect { result ->
                _bankState.value = result
            }
        }
    }


        fun loadInvoiceDetail(invoiceId: Int) {
            viewModelScope.launch {
                getInvoiceByIdUseCase(invoiceId).collect { result ->
                    _invoiceState.value = result
                }
            }
        }

        fun loadActiveVouchers() {
            viewModelScope.launch {
                // Lấy trực tiếp danh sách Voucher đang hoạt động từ Backend
                getActiveVouchersUseCase().collect { result ->
                    _vouchersState.value = result
                }
            }
        }

}