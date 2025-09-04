package com.example.cofee_shop.domain.usecases.orders

import com.example.cofee_shop.domain.repositories.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, status: String, paymentStatus: String) {
        orderRepository.updateOrderStatus(orderId, status, paymentStatus)
    }
}