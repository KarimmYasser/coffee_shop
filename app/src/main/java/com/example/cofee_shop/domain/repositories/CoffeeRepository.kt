package com.example.cofee_shop.domain.repositories

import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee

interface CoffeeRepository {
    suspend fun getHotCoffees(): ApiResult<List<Coffee>>
    suspend fun getIcedCoffees(): ApiResult<List<Coffee>>
    suspend fun getCoffeeById(id: Int, isHot: Boolean): ApiResult<Coffee?>
    suspend fun searchCoffee(query: String): ApiResult<List<Coffee>>
}