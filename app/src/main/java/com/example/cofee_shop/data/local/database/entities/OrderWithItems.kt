package com.example.cofee_shop.data.local.database.entities

data class OrderWithItems(
    val order: OrderEntity,
    val items: List<OrderItemEntity>
)