package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetCoffeeByIdUseCase @Inject constructor(
    private val coffeeRepository: CoffeeRepository
) {
    suspend operator fun invoke(id: Int, isHot: Boolean): ApiResult<Coffee?> {
        return coffeeRepository.getCoffeeById(id, isHot)
    }
}