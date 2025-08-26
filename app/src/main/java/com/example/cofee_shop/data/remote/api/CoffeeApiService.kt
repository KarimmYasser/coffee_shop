package com.example.cofee_shop.data.remote.api

import com.example.cofee_shop.core.Constants
import com.example.cofee_shop.data.remote.dto.HotCoffeeDto
import com.example.cofee_shop.data.remote.dto.IcedCoffeeDto
import retrofit2.http.GET

interface CoffeeApiService {
    @GET(Constants.HOT_COFFEE_ENDPOINT)
    suspend fun getHotCoffees(): List<HotCoffeeDto>

    @GET(Constants.ICED_COFFEE_ENDPOINT)
    suspend fun getIcedCoffees(): List<IcedCoffeeDto>
}