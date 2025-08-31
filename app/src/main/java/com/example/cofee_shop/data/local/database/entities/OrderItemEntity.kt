package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["orderId"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val itemId: Long = 0,
    val orderId: String,
    val drinkId: Int,
    val drinkName: String,
    val drinkImage: String,
    val price: Double,
    val quantity: Int
)