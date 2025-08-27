package com.example.cofee_shop.domain.repositories

import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun isFavorite(drinkId: Int): Boolean
    suspend fun addFavorite(favorite: FavoriteEntity)
    suspend fun removeFavorite(drinkId: Int)
    suspend fun getFavoriteIds(): Flow<List<Int>>
}