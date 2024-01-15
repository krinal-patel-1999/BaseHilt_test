package com.base.hilt.ui.cleanArchitecher.presentation

import GetCategoriesUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.base.hilt.ui.cleanArchitecher.domain.model.Category
import com.base.hilt.utils.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel()  {


    private val _categoryState = MutableStateFlow<ResponseHandler<List<Category>>>(ResponseHandler.Loading())
    val categoryState: StateFlow<ResponseHandler<List<Category>>> get() = _categoryState

    private val _filteredCategories = MutableStateFlow<List<Category>>(emptyList())
    val filteredCategories: StateFlow<List<Category>> get() = _filteredCategories

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { responseHandler ->
                when (responseHandler) {
                    is ResponseHandler.Success -> {
                        _filteredCategories.value = responseHandler.data!!
                        _categoryState.value = responseHandler
                    }
                    is ResponseHandler.Error -> {
                        // Handle error state
                        _categoryState.value = responseHandler
                    }
                    is ResponseHandler.Loading -> {
                        // Handle loading state
                    }
                }
            }
        }
    }

    fun filterCategories(query: String) {
        viewModelScope.launch {
            _filteredCategories.value = _filteredCategories.value.filter {
                it.strCategory.contains(query, ignoreCase = true)
            }
        }
    }
}