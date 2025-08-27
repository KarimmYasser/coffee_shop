package com.example.cofee_shop.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cofee_shop.data.local.database.entities.CoffeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM coffees WHERE isHot = :isHot")
    fun getDrinksByType(isHot: Boolean): Flow<List<CoffeeEntity>>

    @Query("SELECT * FROM coffees")
    fun getAllDrinks(): Flow<List<CoffeeEntity>>

    @Query("SELECT * FROM coffees WHERE id = :drinkId")
    suspend fun getDrinkById(drinkId: Int): CoffeeEntity?

    @Query("SELECT * FROM coffees WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchDrinks(query: String): Flow<List<CoffeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrinks(drinks: List<CoffeeEntity>)

    @Query("DELETE FROM coffees")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM coffees")
    suspend fun getCount(): Int
}