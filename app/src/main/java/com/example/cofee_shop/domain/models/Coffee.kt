package com.example.cofee_shop.domain.models



import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coffee(
    val id: Int,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val image: String,
    val price: Double,
    val isHot: Boolean = true
) : Parcelable