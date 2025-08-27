package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.coffee.CoffeeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val coffeeUseCases: CoffeeUseCases
) : ViewModel() {

    private val _hotCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())
    private val _icedCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())

    private val _currentCategory = MutableStateFlow(CoffeeCategory.ICED)
    val currentCategory: StateFlow<CoffeeCategory> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val filteredCoffeeList: StateFlow<List<Coffee>> = combine(
        _hotCoffeeList,
        _icedCoffeeList,
        _currentCategory,
        _searchQuery
    ) { hotList, icedList, category, query ->
        val sourceList = when (category) {
            CoffeeCategory.HOT -> hotList
            CoffeeCategory.ICED -> icedList
        }

        if (query.isBlank()) {
            sourceList
        } else {
            sourceList.filter { coffee ->
                coffee.title.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadCoffeeData()
    }

    private fun loadCoffeeData() {
        loadHotCoffee()
        loadIcedCoffee()
    }

    private fun loadHotCoffee() {
        viewModelScope.launch {
            when (val result = coffeeUseCases.getHotCoffees()) {
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
                is ApiResult.Success -> {
                    _hotCoffeeList.value = result.data
                    _isLoading.value = false
                    _errorMessage.value = null
                }
                is ApiResult.Failure -> {
                    _errorMessage.value = "Failed to load hot coffee: ${result.exception.localizedMessage}"
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadIcedCoffee() {
        viewModelScope.launch {
            when (val result = coffeeUseCases.getIcedCoffees()) {
                is ApiResult.Loading -> {
                    _isLoading.value = true
                }
                is ApiResult.Success -> {
                    _icedCoffeeList.value = result.data
                    _isLoading.value = false
                    _errorMessage.value = null
                }
                is ApiResult.Failure -> {
                    _errorMessage.value = "Failed to load iced coffee: ${result.exception.localizedMessage}"
                    _isLoading.value = false
                }
            }
        }
    }

    fun setCategory(category: CoffeeCategory) {
        if (_currentCategory.value != category) {
            _currentCategory.value = category
            clearSearch() // Clear search when switching categories
        }
    }

    fun searchCoffee(query: String) {
        _searchQuery.value = query.trim()
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun refreshCurrentCategory() {
        when (_currentCategory.value) {
            CoffeeCategory.HOT -> loadHotCoffee()
            CoffeeCategory.ICED -> loadIcedCoffee()
        }
    }
}

enum class CoffeeCategory {
    HOT, ICED
}