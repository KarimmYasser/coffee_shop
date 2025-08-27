package com.example.cofee_shop.domain.usecases.coffee

import com.example.cofee_shop.domain.repositories.CoffeeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDrinksCountUseCase @Inject constructor(private val coffeeRepository: CoffeeRepository){
    suspend operator fun invoke() = coffeeRepository.getCount()
}