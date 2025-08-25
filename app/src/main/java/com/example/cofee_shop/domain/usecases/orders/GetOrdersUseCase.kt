package com.example.cofee_shop.domain.usecases.orders

import com.example.cofee_shop.domain.repositories.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetOrdersUseCase @Inject constructor(OrderRepository: OrderRepository) {
}