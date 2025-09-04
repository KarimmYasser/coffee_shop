package com.example.cofee_shop.domain.usecases.orders

import com.example.cofee_shop.data.local.database.entities.PaymentRequest
import com.example.cofee_shop.domain.repositories.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(paymentRequest: PaymentRequest): Boolean {
        return orderRepository.processPayment(paymentRequest)
    }
}