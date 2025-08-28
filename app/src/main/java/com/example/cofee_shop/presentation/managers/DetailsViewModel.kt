package com.example.cofee_shop.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.repositories.FavoriteRepository
import com.example.cofee_shop.domain.repositories.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _orderPlaced = MutableStateFlow(false)
    val orderPlaced: StateFlow<Boolean> = _orderPlaced.asStateFlow()

    private lateinit var coffee: Coffee

    fun setCoffee(coffee: Coffee) {
        this.coffee = coffee
        viewModelScope.launch {
            try {
                _isFavorite.value = favoriteRepository.isFavorite(coffee.id)
            } catch (e: Exception) {
                _isFavorite.value = false
            }
        }
    }

    fun increaseQuantity() {
        _quantity.value += 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) _quantity.value -= 1
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                if (_isFavorite.value) {
                    favoriteRepository.removeFavorite(coffee.id)
                } else {
                    favoriteRepository.addFavorite(FavoriteEntity(drinkId = coffee.id))
                }
                _isFavorite.value = !_isFavorite.value
            } catch (e: Exception) {
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            try {
                val orderId = UUID.randomUUID().toString()
                val now = System.currentTimeMillis()
                val total = coffee.price * _quantity.value
                val order = OrderEntity(orderId, now, total)
                val item = OrderItemEntity(
                    orderId = orderId,
                    drinkId = coffee.id,
                    drinkName = coffee.title,
                    drinkImage = coffee.image,
                    price = coffee.price,
                    quantity = _quantity.value
                )
                orderRepository.placeOrder(order, listOf(item))
                _orderPlaced.value = true
            } catch (e: Exception) {
            }
        }
    }
}