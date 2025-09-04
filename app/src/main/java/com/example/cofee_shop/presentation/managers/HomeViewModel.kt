package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.coffee.GetAllDrinksUseCase
import com.example.cofee_shop.domain.usecases.user.GetUserNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllDrinksUseCase: GetAllDrinksUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _allCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName.asStateFlow()

    val coffeeList: StateFlow<List<Coffee>> = _allCoffeeList.asStateFlow()

    init {
        loadUserName()
        loadCoffeeData()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            try {
                val name = getUserNameUseCase()
                _userName.value = name
            } catch (e: Exception) {
                // Handle error silently or log it
                _userName.value = null
            }
        }
    }

    private fun loadCoffeeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllDrinksUseCase().collect { coffeeEntities ->
                    _allCoffeeList.value = coffeeEntities.mapIndexed { index, entity ->
                        Coffee(
                            entity.id,
                            entity.title,
                            entity.description,
                            entity.ingredients,
                            entity.imageUrl,
                            price = when (index % 4) {
                                0 -> 4.5
                                1 -> 5.0
                                2 -> 4.0
                                else -> 5.5
                            },
                            isHot = true
                        )
                    }
                    _isLoading.value = false
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load coffees: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }
}