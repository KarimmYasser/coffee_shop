package com.example.cofee_shop.domain.repositories



import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.data.local.database.entities.CoffeeEntity
import com.example.cofee_shop.domain.models.Coffee
import kotlinx.coroutines.flow.Flow

interface CoffeeRepository {
    suspend fun getHotCoffees(): ApiResult<List<Coffee>>
    suspend fun getIcedCoffees(): ApiResult<List<Coffee>>
    suspend fun getCoffeeById(id: Int, isHot: Boolean): ApiResult<Coffee?>
    suspend fun searchCoffee(query: String): ApiResult<List<Coffee>>

    fun getDrinksByType(isHot: Boolean): Flow<List<CoffeeEntity>>
    fun getAllDrinks(): Flow<List<CoffeeEntity>>
    suspend fun getDrinkById(drinkId: Int): CoffeeEntity?
    fun searchDrinks(query: String): Flow<List<CoffeeEntity>>
    suspend fun insertDrinks(drinks: List<CoffeeEntity>)
    suspend fun clearAll()
    suspend fun getCount(): Int
}