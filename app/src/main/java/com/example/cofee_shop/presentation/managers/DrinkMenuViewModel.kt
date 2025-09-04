package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.usecases.coffee.GetHotCoffeesUseCase
import com.example.cofee_shop.domain.usecases.coffee.GetIcedCoffeesUseCase
import com.example.cofee_shop.domain.usecases.orders.PlaceOrderUseCase
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
    private val getHotCoffeesUseCase: GetHotCoffeesUseCase,
    private val getIcedCoffeesUseCase: GetIcedCoffeesUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    private val _hotCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())
    private val _icedCoffeeList = MutableStateFlow<List<Coffee>>(emptyList())

    private val _currentCategory = MutableStateFlow(CoffeeCategory.ICED)
    val currentCategory: StateFlow<CoffeeCategory> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAddingToCart = MutableStateFlow(false)
    val isAddingToCart: StateFlow<Boolean> = _isAddingToCart.asStateFlow()

    private val _addToCartMessage = MutableStateFlow<String?>(null)
    val addToCartMessage: StateFlow<String?> = _addToCartMessage.asStateFlow()

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
            when (val result = getHotCoffeesUseCase()) {
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
            when (val result = getIcedCoffeesUseCase()) {
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
            clearSearch()
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

    fun clearAddToCartMessage() {
        _addToCartMessage.value = null
    }

    fun refreshCurrentCategory() {
        when (_currentCategory.value) {
            CoffeeCategory.HOT -> loadHotCoffee()
            CoffeeCategory.ICED -> loadIcedCoffee()
        }
    }

    fun addCoffeeToCart(coffee: Coffee, quantity: Int = 1) {
        viewModelScope.launch {
            try {
                _isAddingToCart.value = true
                _errorMessage.value = null

                val order = OrderEntity(
                    orderId = generateOrderId(),
                    totalAmount = coffee.price * quantity,
                    status = "pending",
                    placedAt = System.currentTimeMillis(),
                )

                val orderItem = OrderItemEntity(
                    orderItemId = generateOrderItemId(),
                    orderId = order.orderId,
                    coffeeId = coffee.id.toString(),
                    coffeeName = coffee.title,
                    quantity = quantity,
                    price = coffee.price,
                )

                placeOrderUseCase(order, listOf(orderItem))

                _addToCartMessage.value = "Added ${coffee.title} to cart successfully!"
                _isAddingToCart.value = false

            } catch (e: Exception) {
                _errorMessage.value = "Failed to add to cart: ${e.localizedMessage}"
                _isAddingToCart.value = false
            }
        }
    }

    private fun generateOrderId(): String {
        return "order_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    private fun generateOrderItemId(): String {
        return "item_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }


}

enum class CoffeeCategory {
    HOT, ICED
}