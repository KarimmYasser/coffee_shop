package com.example.cofee_shop.data.models




import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
data class CoffeeItem(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val image: String = "",
    val price: Double = 0.0
) : java.io.Serializable