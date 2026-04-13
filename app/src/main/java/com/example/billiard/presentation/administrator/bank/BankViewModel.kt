package com.example.billiard.presentation.administrator.bank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Bank
import com.example.billiard.domain.model.CreateBankParam
import com.example.billiard.domain.model.UpdateBankParam
import com.example.billiard.domain.model.VietQr
import com.example.billiard.domain.usecase.bank.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankViewModel @Inject constructor(
    private val getBanksUseCase: GetBanksUseCase,
    private val createBankUseCase: CreateBankUseCase,
    private val updateBankUseCase: UpdateBankUseCase,
    private val deleteBankUseCase: DeleteBankUseCase,
    private val getVietQrUseCase: GetVietQrUseCase
) : ViewModel() {

    private val _banksState = MutableStateFlow<Resource<List<Bank>>?>(null)
    val banksState: StateFlow<Resource<List<Bank>>?> = _banksState.asStateFlow()

    private val _vietQrState = MutableStateFlow<Resource<VietQr>?>(null)
    val vietQrState: StateFlow<Resource<VietQr>?> = _vietQrState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    init {
        loadBanks()
    }

    fun loadBanks() {
        viewModelScope.launch {
            getBanksUseCase().collect { result ->
                _banksState.value = result
            }
        }
    }

    fun createBank(param: CreateBankParam) {
        viewModelScope.launch {
            createBankUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    loadBanks()
                }
            }
        }
    }

    fun updateBank(param: UpdateBankParam) {
        viewModelScope.launch {
            updateBankUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    loadBanks()
                }
            }
        }
    }

    fun deleteBank(id: Int) {
        viewModelScope.launch {
            deleteBankUseCase(id).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    delay(500)
                    loadBanks()
                }
            }
        }
    }

    fun getVietQr(id: Int) {
        viewModelScope.launch {
            getVietQrUseCase(id).collect { result ->
                _vietQrState.value = result
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}