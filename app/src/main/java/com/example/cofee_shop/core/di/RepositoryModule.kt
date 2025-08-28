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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCoffeeRepository(
        coffeeRepositoryImpl: CoffeeRepositoryImpl
    ): CoffeeRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
