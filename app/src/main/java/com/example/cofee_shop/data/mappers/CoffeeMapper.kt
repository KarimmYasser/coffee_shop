package com.example.cofee_shop.data.mappers

import com.example.cofee_shop.core.Constants
import com.example.cofee_shop.data.remote.dto.HotCoffeeDto
import com.example.cofee_shop.data.remote.dto.IcedCoffeeDto
import com.example.cofee_shop.domain.models.Coffee
import kotlin.random.Random

object CoffeeMapper {
    private fun generateRandomPrice(): Double {
        return Random.nextDouble(Constants.MIN_PRICE, Constants.MAX_PRICE)
    }

    fun mapHotCoffeeDtoToCoffee(hotCoffeeDto: HotCoffeeDto): Coffee {
        return Coffee(
            id = hotCoffeeDto.id,
            title = hotCoffeeDto.title,
            description = hotCoffeeDto.description,
            ingredients = hotCoffeeDto.ingredients,
            image = hotCoffeeDto.image,
            price = generateRandomPrice(),
            isHot = true
        )
    }

    fun mapIcedCoffeeDtoToCoffee(icedCoffeeDto: IcedCoffeeDto): Coffee {
        return Coffee(
            id = icedCoffeeDto.id,
            title = icedCoffeeDto.title,
            description = icedCoffeeDto.description,
            ingredients = icedCoffeeDto.ingredients,
            image = icedCoffeeDto.image,
            price = generateRandomPrice(),
            isHot = false
        )
    }

    fun mapHotCoffeeListToCoffeeList(hotCoffees: List<HotCoffeeDto>): List<Coffee> {
        return hotCoffees.map { mapHotCoffeeDtoToCoffee(it) }
    }

    fun mapIcedCoffeeListToCoffeeList(icedCoffees: List<IcedCoffeeDto>): List<Coffee> {
        return icedCoffees.map { mapIcedCoffeeDtoToCoffee(it) }
    }
}
