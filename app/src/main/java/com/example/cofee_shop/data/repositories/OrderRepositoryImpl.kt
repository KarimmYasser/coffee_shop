package com.example.cofee_shop.data.repositories

import com.example.cofee_shop.data.local.database.dao.OrderDao
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.data.local.database.entities.PaymentRequest
import com.example.cofee_shop.domain.repositories.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override suspend fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }

    override suspend fun getRecentOrders(): Flow<List<OrderEntity>> {
        return orderDao.getRecentOrders()
    }

    override suspend fun getPastOrders(): Flow<List<OrderEntity>> {
        return orderDao.getPastOrders()
    }

    override suspend fun getOrderItems(orderId: String): List<OrderItemEntity> {
        return orderDao.getOrderItems(orderId)
    }

    override suspend fun getOrderById(orderId: String): OrderEntity? {
        return orderDao.getOrderById(orderId)
    }

    override suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        orderDao.insertOrderWithItems(order, items)
    }

    override suspend fun updateOrderStatus(orderId: String, status: String, paymentStatus: String) {
        orderDao.updateOrderStatus(orderId, status, paymentStatus)
    }

    override suspend fun deleteOrder(orderId: String) {
        orderDao.deleteOrderWithItems(orderId)
    }

    override suspend fun processPayment(paymentRequest: PaymentRequest): Boolean {
        // Simulate payment processing
        delay(2000) // Simulate network delay
        return (1..100).random() > 10 // 90% success rate
    }
}