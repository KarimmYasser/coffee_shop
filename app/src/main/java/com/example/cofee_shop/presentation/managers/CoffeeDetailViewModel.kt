package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.favourites.AddFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.IsFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.RemoveFavoriteUseCase
import com.example.cofee_shop.domain.usecases.orders.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CoffeeDetailViewModel @Inject constructor(
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val getFavoriteByIdUseCase: IsFavoriteUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
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
                    removeFavoriteUseCase(coffee.id)
                    _isFavorite.value = false
                    _uiState.value = _uiState.value.copy(
                        message = "Removed from favorites"
                    )
                } else {
                    val favoriteEntity = FavoriteEntity(
                        drinkId = coffee.id,
                        title = coffee.title,
                        price = coffee.price,
                        imageUrl = coffee.image,
                        description = coffee.description,
                        ingredients = coffee.ingredients,
                        isHot = coffee.isHot
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
        if (_quantity.value < 99) { // Set a reasonable maximum
            _quantity.value = _quantity.value + 1
        }
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value = _quantity.value - 1
        }
    }

    fun createOrder(
        coffee: Coffee,
        quantity: Int,
        totalPrice: Double,
        onOrderCreated: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val orderId = generateOrderId()
                val subtotal = coffee.price * quantity
                val totalAmount = calculateTotalAmount(subtotal)

                val order = OrderEntity(
                    orderId = orderId,
                    totalAmount = totalAmount,
                    status = "pending",
                    paymentStatus = "unpaid",
                    placedAt = System.currentTimeMillis(),
                    subtotal = subtotal
                )

                val orderItem = OrderItemEntity(
                    orderItemId = generateOrderItemId(),
                    orderId = orderId,
                    coffeeId = coffee.id.toString(),
                    coffeeName = coffee.title,
                    quantity = quantity,
                    price = coffee.price,
                    imageUrl = coffee.image
                )

                placeOrderUseCase(order, listOf(orderItem))
                // Create the order using the use case


                    onOrderCreated(orderId)


            } catch (e: Exception) {
                onError("Failed to create order: ${e.localizedMessage}")
            }
        }
    }
    private fun calculateTotalAmount(subtotal: Double): Double {
        val deliveryFee = 3000.0
        val packagingFee = 5000.0
        return subtotal + deliveryFee + packagingFee
    }

    private fun generateOrderId(): String {
        return "order_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
    private fun generateOrderItemId(): String {
        return "item_${System.currentTimeMillis()}_${(1000..9999).random()}"
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