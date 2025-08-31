package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.usecases.orders.GetOrdersUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrderByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private val _orders = MutableStateFlow<List<OrderEntity>>(emptyList())
    val orders: StateFlow<List<OrderEntity>> = _orders.asStateFlow()

    private val _orderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val orderItems: StateFlow<List<OrderItemEntity>> = _orderItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = getOrdersUseCase.invoke()
                result.collect { ordersList ->
                    _orders.value = ordersList
                }
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
                _isLoading.value = false
            }
        }
    }

    fun loadOrderItems(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val items = getOrderByIdUseCase(orderId)
                _orderItems.value = items
                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}