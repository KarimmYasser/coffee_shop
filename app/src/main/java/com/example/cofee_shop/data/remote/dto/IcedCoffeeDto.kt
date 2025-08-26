package com.example.cofee_shop.data.remote.dto

data class IcedCoffeeDto(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val image: String
)