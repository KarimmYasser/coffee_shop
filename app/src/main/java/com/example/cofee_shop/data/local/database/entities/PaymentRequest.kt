package com.example.cofee_shop.data.local.database.entities

data class PaymentRequest(
    val orderId: String,
    val amount: Double,
    val paymentMethod: String = "card"
)