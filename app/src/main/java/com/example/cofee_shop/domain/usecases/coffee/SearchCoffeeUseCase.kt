package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchCoffeeUseCase @Inject constructor(
    private val coffeeRepository: CoffeeRepository
) {
    suspend operator fun invoke(query: String): ApiResult<List<Coffee>> {
        if (query.isBlank()) {
            return ApiResult.Success(emptyList())
        }
        return coffeeRepository.searchCoffee(query)
    }
}