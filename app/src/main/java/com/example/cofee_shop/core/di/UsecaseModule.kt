package com.example.cofee_shop.core.di

import com.example.cofee_shop.domain.repositories.CoffeeRepository
import com.example.cofee_shop.domain.repositories.FavoriteRepository
import com.example.cofee_shop.domain.repositories.OrderRepository
import com.example.cofee_shop.domain.repositories.UserRepository
import com.example.cofee_shop.domain.usecases.coffee.GetCoffeeByIdUseCase
import com.example.cofee_shop.domain.usecases.coffee.GetHotCoffeesUseCase
import com.example.cofee_shop.domain.usecases.coffee.GetIcedCoffeesUseCase
import com.example.cofee_shop.domain.usecases.coffee.SearchCoffeeUseCase
import com.example.cofee_shop.domain.usecases.favourites.AddFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.GetFavoritesUseCase
import com.example.cofee_shop.domain.usecases.favourites.IsFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.RemoveFavoriteUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrderByIdUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrdersUseCase
import com.example.cofee_shop.domain.usecases.orders.PlaceOrderUseCase
import com.example.cofee_shop.domain.usecases.user.GetUserNameUseCase
import com.example.cofee_shop.domain.usecases.user.SaveUserNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetHotCoffeesUseCase(
        coffeeRepository: CoffeeRepository
    ): GetHotCoffeesUseCase = GetHotCoffeesUseCase(coffeeRepository)

    @Provides
    @Singleton
    fun provideGetIcedCoffeesUseCase(
        coffeeRepository: CoffeeRepository
    ): GetIcedCoffeesUseCase = GetIcedCoffeesUseCase(coffeeRepository)

    @Provides
    @Singleton
    fun provideGetCoffeeByIdUseCase(
        coffeeRepository: CoffeeRepository
    ): GetCoffeeByIdUseCase = GetCoffeeByIdUseCase(coffeeRepository)

    @Provides
    @Singleton
    fun provideSearchCoffeeUseCase(
        coffeeRepository: CoffeeRepository
    ): SearchCoffeeUseCase = SearchCoffeeUseCase(coffeeRepository)

    @Provides
    @Singleton
    fun provideIsFavoriteUseCase(
        favoriteRepository: FavoriteRepository
    ): IsFavoriteUseCase = IsFavoriteUseCase(favoriteRepository)

    @Provides
    @Singleton
    fun provideAddFavoriteUseCase(
        favoriteRepository: FavoriteRepository
    ): AddFavoriteUseCase = AddFavoriteUseCase(favoriteRepository)

    @Provides
    @Singleton
    fun provideRemoveFavoriteUseCase(
        favoriteRepository: FavoriteRepository
    ): RemoveFavoriteUseCase = RemoveFavoriteUseCase(favoriteRepository)

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(
        favoriteRepository: FavoriteRepository
    ): GetFavoritesUseCase = GetFavoritesUseCase(favoriteRepository)

    @Provides
    @Singleton
    fun provideGetOrderByIdUseCase(
        orderRepository: OrderRepository
    ): GetOrderByIdUseCase = GetOrderByIdUseCase(orderRepository)

    @Provides
    @Singleton
    fun providePlaceOrderUseCase(
        orderRepository: OrderRepository
    ): PlaceOrderUseCase = PlaceOrderUseCase(orderRepository)

    @Provides
    @Singleton
    fun provideGetOrdersUseCase(
        orderRepository: OrderRepository
    ): GetOrdersUseCase = GetOrdersUseCase(orderRepository)

    @Provides
    @Singleton
    fun provideSaveUserNameUseCase(
        userRepository: UserRepository
    ): SaveUserNameUseCase = SaveUserNameUseCase(userRepository)

    @Provides
    @Singleton
    fun provideGetUserNameUseCase(
        userRepository: UserRepository
    ): GetUserNameUseCase = GetUserNameUseCase(userRepository)
}