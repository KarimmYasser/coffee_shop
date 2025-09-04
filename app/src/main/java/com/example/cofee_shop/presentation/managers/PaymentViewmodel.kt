package com.example.cofee_shop.presentation.managers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.data.local.database.entities.PaymentRequest
import com.example.cofee_shop.domain.usecases.orders.GetOrderByIdUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrderItemsUseCase
import com.example.cofee_shop.domain.usecases.orders.ProcessPaymentUseCase
import com.example.cofee_shop.domain.usecases.orders.UpdateOrderStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getOrderItemsUseCase: GetOrderItemsUseCase
) : ViewModel() {

    private val _orderDetails = MutableStateFlow<OrderEntity?>(null)
    val orderDetails: StateFlow<OrderEntity?> = _orderDetails.asStateFlow()

    private val _orderItems = MutableStateFlow<List<OrderItemEntity>>(emptyList())
    val orderItems: StateFlow<List<OrderItemEntity>> = _orderItems.asStateFlow()

    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _paymentResult = MutableStateFlow<PaymentResult?>(null)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadOrderDetails(orderId: String) {
        viewModelScope.launch {
            try {
                val order = getOrderByIdUseCase(orderId)
                val items = getOrderItemsUseCase(orderId)

                _orderDetails.value = order
                _orderItems.value = items
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load order details: ${e.localizedMessage}"
            }
        }
    }

    fun processPayment(orderId: String, amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            try {
                _isProcessingPayment.value = true
                _errorMessage.value = null

                val paymentRequest = PaymentRequest(orderId, amount, paymentMethod)
                val success = processPaymentUseCase(paymentRequest)

                if (success) {
                    updateOrderStatusUseCase(orderId, "paid", "paid")
                    _paymentResult.value = PaymentResult.Success("Payment successful!")
                } else {
                    updateOrderStatusUseCase(orderId, "pending", "failed")
                    _paymentResult.value = PaymentResult.Failed("Payment failed. Please try again.")
                }

                _isProcessingPayment.value = false
            } catch (e: Exception) {
                _errorMessage.value = "Payment processing error: ${e.localizedMessage}"
                _isProcessingPayment.value = false
                _paymentResult.value = PaymentResult.Failed("Payment failed due to an error.")
            }
        }
    }

    fun clearPaymentResult() {
        _paymentResult.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
sealed class PaymentResult {
    data class Success(val message: String) : PaymentResult()
    data class Failed(val message: String) : PaymentResult()
}