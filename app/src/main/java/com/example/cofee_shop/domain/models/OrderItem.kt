package com.example.cofee_shop.domain.models

data class CartItem(
    val coffeeId: String,
    val coffeeName: String,
    val coffeePrice: Double,
    val coffeeImageUrl: String?,
    val quantity: Int = 1
)