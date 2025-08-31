package com.example.cofee_shop.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class Coffee(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val image: String,
    val price: Double,
    val isHot: Boolean = true
) : java.io.Serializable