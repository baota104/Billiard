package com.example.billiard.presentation.administrator.time

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
        viewModelScope.launch {
            createPriceListUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadPriceLists()
                }
            }
        }
    }

    fun updatePriceList(param: UpdatePriceListParam) {
        viewModelScope.launch {
            updatePriceListUseCase(param).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadPriceLists()
                }
            }
        }
    }

    fun deletePriceList(id: Int) {
        viewModelScope.launch {
            deletePriceListUseCase(id).collect { result ->
                _actionState.value = result
                if (result is Resource.Success) {
                    loadPriceLists()
                }
            }
        }
    }

    fun resetActionState() {
        _actionState.value = null
    }
}