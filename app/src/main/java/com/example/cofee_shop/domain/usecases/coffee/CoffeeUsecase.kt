package com.example.cofee_shop.domain.usecases.coffee

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class CoffeeUseCases @Inject constructor(
    val getHotCoffees: GetHotCoffeesUseCase,
    val getIcedCoffees: GetIcedCoffeesUseCase,
    val getCoffeeById: GetCoffeeByIdUseCase,
    val searchCoffee: SearchCoffeeUseCase,
)