package com.example.billiard.presentation.administrator.voucher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreateVoucherParam
import com.example.billiard.domain.model.UpdateVoucherParam
import com.example.billiard.domain.model.Voucher
import com.example.billiard.domain.usecase.voucher.CreateVoucherUseCase
import com.example.billiard.domain.usecase.voucher.DeleteVoucherUseCase
import com.example.billiard.domain.usecase.voucher.GetActiveVouchersUseCase
import com.example.billiard.domain.usecase.voucher.GetVoucherByIdUseCase
import com.example.billiard.domain.usecase.voucher.UpdateVoucherUseCase
import com.example.billiard.domain.usecase.voucher.ValidateVouchersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VoucherViewModel @Inject constructor(
    private val getActiveVouchersUseCase: GetActiveVouchersUseCase,
    private val getVoucherByIdUseCase: GetVoucherByIdUseCase,
    private val createVoucherUseCase: CreateVoucherUseCase,
    private val updateVoucherUseCase: UpdateVoucherUseCase,
    private val validateVouchersUseCase: ValidateVouchersUseCase,
    private val deleteVoucherUseCase: DeleteVoucherUseCase
) : ViewModel() {

    // Lắng nghe danh sách Voucher
    private val _vouchersState = MutableStateFlow<Resource<List<Voucher>>?>(null)
    val vouchersState: StateFlow<Resource<List<Voucher>>?> = _vouchersState.asStateFlow()

    // Lắng nghe chi tiết Voucher
    private val _voucherDetailState = MutableStateFlow<Resource<Voucher>?>(null)
    val voucherDetailState: StateFlow<Resource<Voucher>?> = _voucherDetailState.asStateFlow()

    // Trạng thái các Action: Create / Update / Delete
    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    init {
        loadActiveVouchers()
    }

    fun loadActiveVouchers() {
        viewModelScope.launch {
            getActiveVouchersUseCase().collect { result ->
                _vouchersState.value = result
            }
        }
    }

    fun getVoucherById(id: Long) {
        viewModelScope.launch {
            getVoucherByIdUseCase(id).collect { result ->
                _voucherDetailState.value = result
            }
        }
    }

    fun createVoucher(param: CreateVoucherParam) {
        viewModelScope.launch {
            createVoucherUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadActiveVouchers()
                }
            }
        }
    }

    fun updateVoucher(param: UpdateVoucherParam) {
        viewModelScope.launch {
            updateVoucherUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadActiveVouchers()
                }
            }
        }
    }

    fun deleteVoucher(id: Long) {
        viewModelScope.launch {
            deleteVoucherUseCase(id).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadActiveVouchers()
                }
            }
        }
    }

    // Dùng để validate list voucher cho người dùng khi chuẩn bị thanh toán
    fun validateVouchers(totalBillAmount: Double) {
        viewModelScope.launch {
            validateVouchersUseCase(totalBillAmount).collect { result ->
                _vouchersState.value = result
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}