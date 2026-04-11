package com.example.billiard.presentation.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.billiard.core.network.Resource
import com.example.billiard.domain.model.Category
import com.example.billiard.domain.usecase.category.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {

    private val _categoriesState = MutableStateFlow<Resource<List<Category>>?>(null)
    val categoriesState: StateFlow<Resource<List<Category>>?> = _categoriesState.asStateFlow()

    private val _actionState = MutableStateFlow<Resource<Category>?>(null)
    val actionState: StateFlow<Resource<Category>?> = _actionState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                _categoriesState.value = result
            }
        }
    }


    fun resetActionState() {
        _actionState.value = null
    }
}