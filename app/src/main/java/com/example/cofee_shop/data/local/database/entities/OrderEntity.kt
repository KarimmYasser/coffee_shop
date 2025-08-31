package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val totalAmount: Double,
    val placedAt: Long,
    val status: String
)