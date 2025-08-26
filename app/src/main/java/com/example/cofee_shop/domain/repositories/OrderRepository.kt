package com.example.cofee_shop.domain.repositories

import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getAllOrders(): Flow<List<OrderEntity>>
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity>
    suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>)
    suspend fun deleteOrder(orderId: String)
}