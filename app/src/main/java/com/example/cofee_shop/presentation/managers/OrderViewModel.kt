package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderFilter
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.usecases.orders.DeleteOrderUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrderItemsUseCase
import com.example.cofee_shop.domain.usecases.orders.GetPastOrdersUseCase
import com.example.cofee_shop.domain.usecases.orders.GetRecentOrdersUseCase
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
class OrderViewModel @Inject constructor(
    private val getRecentOrdersUseCase: GetRecentOrdersUseCase,
    private val getPastOrdersUseCase: GetPastOrdersUseCase,
    private val getOrderItemsUseCase: GetOrderItemsUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(OrderFilter.RECENTLY)
    val currentFilter: StateFlow<OrderFilter> = _currentFilter.asStateFlow()

    private val _recentOrders = MutableStateFlow<List<OrderEntity>>(emptyList())
    private val _pastOrders = MutableStateFlow<List<OrderEntity>>(emptyList())

    private val _orderItems = MutableStateFlow<Map<String, List<OrderItemEntity>>>(emptyMap())
    val orderItems: StateFlow<Map<String, List<OrderItemEntity>>> = _orderItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val filteredOrders: StateFlow<List<OrderEntity>> = combine(
        _recentOrders,
        _pastOrders,
        _currentFilter
    ) { recentOrders, pastOrders, filter ->
        when (filter) {
            OrderFilter.RECENTLY -> recentOrders
            OrderFilter.PAST_ORDERS -> pastOrders
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        loadOrders()
    }

    fun setFilter(filter: OrderFilter) {
        if (_currentFilter.value != filter) {
            _currentFilter.value = filter
        }
    }

    private fun loadOrders() {
        loadRecentOrders()
        loadPastOrders()
    }

    private fun loadRecentOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                getRecentOrdersUseCase().collect { orders ->
                    _recentOrders.value = orders
                    loadOrderItemsForOrders(orders)
                    _isLoading.value = false
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load recent orders: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    private fun loadPastOrders() {
        viewModelScope.launch {
            try {
                getPastOrdersUseCase().collect { orders ->
                    _pastOrders.value = orders
                    loadOrderItemsForOrders(orders)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load past orders: ${e.localizedMessage}"
            }
        }
    }

    private fun loadOrderItemsForOrders(orders: List<OrderEntity>) {
        viewModelScope.launch {
            val itemsMap = mutableMapOf<String, List<OrderItemEntity>>()
            for (order in orders) {
                try {
                    val items = getOrderItemsUseCase(order.orderId)
                    itemsMap[order.orderId] = items
                } catch (e: Exception) {
                    android.util.Log.e("OrderViewModel", "Failed to load items for order ${order.orderId}", e)
                }
            }

            _orderItems.value = itemsMap
        }
    }

    fun getOrderItemsById(orderId: String): List<OrderItemEntity> {
        return _orderItems.value[orderId] ?: emptyList()
    }

    fun refreshOrders() {
        loadOrders()
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                deleteOrderUseCase(orderId)

                // Remove from local state
                val updatedRecentOrders = _recentOrders.value.filter { it.orderId != orderId }
                val updatedPastOrders = _pastOrders.value.filter { it.orderId != orderId }
                val updatedOrderItems = _orderItems.value.toMutableMap()
                updatedOrderItems.remove(orderId)

                _recentOrders.value = updatedRecentOrders
                _pastOrders.value = updatedPastOrders
                _orderItems.value = updatedOrderItems

                _isLoading.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete order: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}