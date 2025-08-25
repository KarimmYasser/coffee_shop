package com.example.cofee_shop.domain.usecases.orders

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class OrderUseCases @Inject constructor(
    val getOrderById: GetOrderByIdUseCase,
    val placeOrder: PlaceOrderUseCase,
    val getOrders: GetOrdersUseCase
)