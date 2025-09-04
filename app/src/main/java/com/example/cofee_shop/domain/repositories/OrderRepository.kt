package com.example.cofee_shop.domain.repositories

import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.data.local.database.entities.PaymentRequest
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getAllOrders(): Flow<List<OrderEntity>>
    suspend fun getRecentOrders(): Flow<List<OrderEntity>>
    suspend fun getPastOrders(): Flow<List<OrderEntity>>
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>
    suspend fun getOrderById(orderId: String): OrderEntity?
    suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>)
    suspend fun updateOrderStatus(orderId: String, status: String, paymentStatus: String)
    suspend fun deleteOrder(orderId: String)
    suspend fun processPayment(paymentRequest: PaymentRequest): Boolean
}