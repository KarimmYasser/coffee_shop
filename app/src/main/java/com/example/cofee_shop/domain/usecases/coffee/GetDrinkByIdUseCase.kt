package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDrinkByIdUseCase @Inject constructor(private val coffeeRepository: CoffeeRepository) {
    suspend operator fun invoke(id: Int) = coffeeRepository.getDrinkById(id)
}