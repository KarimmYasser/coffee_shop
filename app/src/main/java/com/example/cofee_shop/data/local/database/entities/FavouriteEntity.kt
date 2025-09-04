package com.example.cofee_shop.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val drinkId: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val imageUrl: String,
    val isHot: Boolean,
    val price : Double,
    val addedAt: Long = System.currentTimeMillis()
)