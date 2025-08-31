package com.example.cofee_shop.domain.usecases.orders

import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.data.local.database.entities.OrderItemEntity
import com.example.cofee_shop.domain.repositories.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceOrderUseCase @Inject constructor(private val orderRepository: OrderRepository) {
    suspend operator fun invoke(order: OrderEntity, items: List<OrderItemEntity>) {
        return orderRepository.placeOrder(order, items)
    }
}