package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "coffees")
data class CoffeeEntity (
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val imageUrl: String,
    val isHot: Boolean,
    val cachedAt: Long = System.currentTimeMillis()
)