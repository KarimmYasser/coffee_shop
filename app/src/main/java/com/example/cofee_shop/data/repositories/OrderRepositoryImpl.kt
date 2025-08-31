package com.example.cofee_shop.data.repositories

import com.example.cofee_shop.data.local.database.dao.OrderDao
import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.repositories.OrderRepository
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

    override suspend fun getOrderItems(orderId: String): List<OrderItemEntity> {
        return orderDao.getOrderItems(orderId)
    }

    override suspend fun placeOrder(order: OrderEntity, items: List<OrderItemEntity>) {
        orderDao.insertOrderWithItems(order, items)
    }

    override suspend fun deleteOrder(orderId: String) {
        orderDao.deleteOrder(orderId)
    }
}