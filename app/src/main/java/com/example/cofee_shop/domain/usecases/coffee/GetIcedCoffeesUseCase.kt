package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.core.ApiResult
import com.example.cofee_shop.domain.models.Coffee
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetIcedCoffeesUseCase @Inject constructor(
    private val coffeeRepository: CoffeeRepository
) {
    suspend operator fun invoke(): ApiResult<List<Coffee>> {
        return coffeeRepository.getIcedCoffees()
    }
}