package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.favourites.AddFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.IsFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.RemoveFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeDetailViewModel @Inject constructor(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getFavoriteByIdUseCase: IsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoffeeDetailUiState())
    val uiState: StateFlow<CoffeeDetailUiState> = _uiState.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun initializeCoffee(coffee: Coffee) {
        _uiState.value = _uiState.value.copy(coffee = coffee)
        checkIfFavorite(coffee.id)
    }

    private fun checkIfFavorite(coffeeId: Int) {
        viewModelScope.launch {
            try {
                val favorite = getFavoriteByIdUseCase(coffeeId)
                _isFavorite.value = favorite != null
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
    }

    fun toggleFavorite() {
        val coffee = _uiState.value.coffee ?: return

        viewModelScope.launch {
            try {
                _isLoading.value = true

                if (_isFavorite.value) {
                    // Remove from favorites
                    removeFavoriteUseCase(coffee.id)
                    _isFavorite.value = false
                    _uiState.value = _uiState.value.copy(
                        message = "Removed from favorites"
                    )
                } else {
                    // Add to favorites
                    val favoriteEntity = FavoriteEntity(
                        drinkId = coffee.id,
                        addedAt =  System.currentTimeMillis(
                        )

                    )
                    addFavoriteUseCase(favoriteEntity)
                    _isFavorite.value = true
                    _uiState.value = _uiState.value.copy(
                        message = "Added to favorites"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update favorites: ${e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun increaseQuantity() {
        _quantity.value = _quantity.value + 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value = _quantity.value - 1
        }
    }

    fun buyNow() {
        val coffee = _uiState.value.coffee ?: return
        val currentQuantity = _quantity.value

        _uiState.value = _uiState.value.copy(
            message = "Added $currentQuantity ${coffee.title} to cart",
            shouldNavigateBack = true
        )
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun navigationHandled() {
        _uiState.value = _uiState.value.copy(shouldNavigateBack = false)
    }

    data class CoffeeDetailUiState(
        val coffee: Coffee? = null,
        val message: String? = null,
        val error: String? = null,
        val shouldNavigateBack: Boolean = false
    )
}
