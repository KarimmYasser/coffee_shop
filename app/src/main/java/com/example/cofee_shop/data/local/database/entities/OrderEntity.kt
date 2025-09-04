package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val totalAmount: Double,
    val placedAt: Long,
    val status: String,
    val paymentStatus: String = "unpaid",
    val deliveryFee: Double = 3000.0,
    val packagingFee: Double = 5000.0,
    val subtotal: Double = totalAmount - deliveryFee - packagingFee
)