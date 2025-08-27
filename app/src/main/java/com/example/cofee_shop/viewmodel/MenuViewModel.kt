package com.example.cofee_shop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: CoffeeRepository
) : ViewModel() {

    private val _hotCoffeeList = MutableLiveData<List<Coffee>>()
    val hotCoffeeList: LiveData<List<Coffee>> = _hotCoffeeList

    private val _icedCoffeeList = MutableLiveData<List<Coffee>>()
    val icedCoffeeList: LiveData<List<Coffee>> = _icedCoffeeList

    private val _filteredHotCoffeeList = MutableLiveData<List<Coffee>>()
    val filteredHotCoffeeList: LiveData<List<Coffee>> = _filteredHotCoffeeList

    private val _filteredIcedCoffeeList = MutableLiveData<List<Coffee>>()
    val filteredIcedCoffeeList: LiveData<List<Coffee>> = _filteredIcedCoffeeList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadHotCoffee() {
        if (_hotCoffeeList.value.isNullOrEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                when (val result = repository.getHotCoffees()) {
                    is ApiResult.Success -> {
                        _hotCoffeeList.value = result.data
                        _filteredHotCoffeeList.value = result.data
                    }
                    is ApiResult.Failure -> {
                        _errorMessage.value = "Failed to load hot coffee: ${result.exception.message}"
                    }
                    is ApiResult.Loading -> {
                        // Handle loading state if needed
                    }
                }

                _isLoading.value = false
            }
        }
    }

    fun loadIcedCoffee() {
        if (_icedCoffeeList.value.isNullOrEmpty()) {
            viewModelScope.launch {
                _isLoading.value = true
                _errorMessage.value = null

                when (val result = repository.getIcedCoffees()) {
                    is ApiResult.Success -> {
                        _icedCoffeeList.value = result.data
                        _filteredIcedCoffeeList.value = result.data
                    }
                    is ApiResult.Failure -> {
                        _errorMessage.value = "Failed to load iced coffee: ${result.exception.message}"
                    }
                    is ApiResult.Loading -> {
                        // Handle loading state if needed
                    }
                }

                _isLoading.value = false
            }
        }
    }

    fun searchCoffee(query: String) {
        val hotList = _hotCoffeeList.value ?: emptyList()
        val icedList = _icedCoffeeList.value ?: emptyList()

        if (query.isBlank()) {
            _filteredHotCoffeeList.value = hotList
            _filteredIcedCoffeeList.value = icedList
        } else {
            _filteredHotCoffeeList.value = hotList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.ingredients.any { ingredient ->
                            ingredient.contains(query, ignoreCase = true)
                        }
            }
            _filteredIcedCoffeeList.value = icedList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.ingredients.any { ingredient ->
                            ingredient.contains(query, ignoreCase = true)
                        }
            }
        }
    }

    fun toggleFavorite(coffee: Coffee) {
        // TODO: Implement favorite toggle logic using Room database
        // This will be implemented when you work on the Favorites feature
    }
}