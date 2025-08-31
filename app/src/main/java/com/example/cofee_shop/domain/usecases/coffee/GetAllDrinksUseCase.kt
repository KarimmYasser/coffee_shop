package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllDrinksUseCase @Inject constructor(private val coffeeRepository: CoffeeRepository) {
    suspend operator fun invoke() = coffeeRepository.getAllDrinks()
}