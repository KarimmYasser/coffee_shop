package com.example.cofee_shop.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE drinkId = :drinkId)")
    suspend fun isFavorite(drinkId: Int): Boolean

    @Query("SELECT * FROM favorites WHERE drinkId = :drinkId")
    suspend fun getFavoriteById(drinkId: Int): FavoriteEntity?


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE drinkId = :drinkId")
    suspend fun removeFavorite(drinkId: Int)

    @Query("SELECT drinkId FROM favorites")
    fun getFavoriteIds(): Flow<List<Int>>
}