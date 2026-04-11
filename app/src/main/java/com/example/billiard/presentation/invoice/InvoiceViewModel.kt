package com.example.billiard.presentation.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateInvoiceParam
import com.example.billiard.domain.model.Invoice
import com.example.billiard.domain.model.UpdateInvoiceParam
import com.example.billiard.domain.usecase.invoice.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val getInvoiceByIdUseCase: GetInvoiceByIdUseCase,
    private val createInvoiceUseCase: CreateInvoiceUseCase,
    private val updateInvoiceUseCase: UpdateInvoiceUseCase,
    private val deleteInvoiceUseCase: DeleteInvoiceUseCase
) : ViewModel() {

    private val _invoicesState = MutableStateFlow<Resource<List<Invoice>>?>(null)
    val invoicesState: StateFlow<Resource<List<Invoice>>?> = _invoicesState.asStateFlow()

    private val _invoiceDetailState = MutableStateFlow<Resource<Invoice>?>(null)
    val invoiceDetailState: StateFlow<Resource<Invoice>?> = _invoiceDetailState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    fun loadInvoices() {
        viewModelScope.launch {
            getInvoicesUseCase().collect { result ->
                _invoicesState.value = result
            }
        }
    }

    fun getInvoiceById(id: Int) {
        viewModelScope.launch {
            getInvoiceByIdUseCase(id).collect { result ->
                _invoiceDetailState.value = result
            }
        }
    }

    fun createInvoice(param: CreateInvoiceParam) {
        viewModelScope.launch {
            createInvoiceUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadInvoices()
                }
            }
        }
    }

    fun updateInvoice(param: UpdateInvoiceParam) {
        viewModelScope.launch {
            updateInvoiceUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadInvoices()
                }
            }
        }
    }

    fun deleteInvoice(id: Int) {
        viewModelScope.launch {
            deleteInvoiceUseCase(id).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadInvoices()
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}