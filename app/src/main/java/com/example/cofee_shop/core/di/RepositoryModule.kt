package com.example.cofee_shop.core.di

import com.example.cofee_shop.data.repositories.CoffeeRepositoryImpl
import com.example.cofee_shop.data.repositories.FavoriteRepositoryImpl
import com.example.cofee_shop.data.repositories.OrderRepositoryImpl
import com.example.cofee_shop.data.repositories.UserRepositoryImpl
import com.example.cofee_shop.domain.repositories.CoffeeRepository
import com.example.cofee_shop.domain.repositories.FavoriteRepository
import com.example.cofee_shop.domain.repositories.OrderRepository
import com.example.cofee_shop.domain.repositories.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindCoffeeRepository(
        coffeeRepositoryImpl: CoffeeRepositoryImpl
    ): CoffeeRepository

    @Binds
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}