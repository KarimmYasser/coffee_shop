package com.example.cofee_shop.domain.usecases.orders

import com.example.cofee_shop.data.local.database.entities.OrderEntity
import com.example.cofee_shop.domain.repositories.OrderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(): Flow<List<OrderEntity>> {
        return orderRepository.getAllOrders()
    }
}