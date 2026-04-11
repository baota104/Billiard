package com.example.billiard.presentation.administrator.time

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.CreatePriceListParam
import com.example.billiard.domain.model.PriceList
import com.example.billiard.domain.model.UpdatePriceListParam
import com.example.billiard.domain.usecase.pricelist.CreatePriceListUseCase
import com.example.billiard.domain.usecase.pricelist.DeletePriceListUseCase
import com.example.billiard.domain.usecase.pricelist.GetPriceListsUseCase
import com.example.billiard.domain.usecase.pricelist.UpdatePriceListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PriceListViewModel @Inject constructor(
    private val getPriceListsUseCase: GetPriceListsUseCase,
    private val createPriceListUseCase: CreatePriceListUseCase,
    private val updatePriceListUseCase: UpdatePriceListUseCase,
    private val deletePriceListUseCase: DeletePriceListUseCase
) : ViewModel() {

    private val _priceListsState = MutableStateFlow<Resource<List<PriceList>>?>(null)
    val priceListsState: StateFlow<Resource<List<PriceList>>?> = _priceListsState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Any>?>(null)
    val actionState: StateFlow<Resource<Any>?> = _actionState.asStateFlow()

    init {
        loadPriceLists()
    }

    fun loadPriceLists() {
        viewModelScope.launch {
            getPriceListsUseCase().collect { result ->
                _priceListsState.value = result
            }
        }
    }

    fun createPriceList(param: CreatePriceListParam) {
        Log.d("PriceListViewModel", "TẠO KHUNG GIỜ: Start=${param.startTime}, End=${param.endTime}, Price=${param.unitPrice}, Type=${param.tableType}")
        viewModelScope.launch {
            createPriceListUseCase(param).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("PriceListViewModel", "TẠO KHUNG GIỜ THÀNH CÔNG")
                        delay(500)
                        loadPriceLists()
                    }
                    is Resource.Error -> {
                        Log.e("PriceListViewModel", "LỖI TẠO KHUNG GIỜ: ${result.message} | Ex: ${result.exception?.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun updatePriceList(param: UpdatePriceListParam) {
        Log.d("PriceListViewModel", "CẬP NHẬT KHUNG GIỜ: ID=${param.id}, Start=${param.startTime}, End=${param.endTime}, Price=${param.unitPrice}, Type=${param.tableType}")
        viewModelScope.launch {
            updatePriceListUseCase(param).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("PriceListViewModel", "CẬP NHẬT KHUNG GIỜ THÀNH CÔNG")
                        delay(500)
                        loadPriceLists()
                    }
                    is Resource.Error -> {
                        Log.e("PriceListViewModel", "LỖI CẬP NHẬT KHUNG GIỜ: ${result.message} | Ex: ${result.exception?.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun deletePriceList(id: Int) {
        Log.d("PriceListViewModel", "XÓA KHUNG GIỜ: ID=${id}")
        viewModelScope.launch {
            deletePriceListUseCase(id).collect { result ->
                _actionState.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("PriceListViewModel", "XÓA KHUNG GIỜ THÀNH CÔNG")
                        delay(500)
                        loadPriceLists()
                    }
                    is Resource.Error -> {
                        Log.e("PriceListViewModel", "LỖI XÓA KHUNG GIỜ: ${result.message} | Ex: ${result.exception?.message}")
                    }
                    is Resource.Loading -> {}
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}