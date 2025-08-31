package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val orderItemId: String,
    val orderId: String,
    val coffeeId: String,
    val coffeeName: String,
    val quantity: Int,
    val price: Double
)