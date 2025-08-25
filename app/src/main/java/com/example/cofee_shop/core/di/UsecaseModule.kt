package com.example.cofee_shop.core.di

import com.example.cofee_shop.domain.usecases.coffee.CoffeeUseCases
import com.example.cofee_shop.domain.usecases.coffee.GetCoffeeByIdUseCase
import com.example.cofee_shop.domain.usecases.coffee.GetHotCoffeesUseCase
import com.example.cofee_shop.domain.usecases.coffee.GetIcedCoffeesUseCase
import com.example.cofee_shop.domain.usecases.coffee.SearchCoffeeUseCase
import com.example.cofee_shop.domain.usecases.favourites.AddFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.FavoriteUseCases
import com.example.cofee_shop.domain.usecases.favourites.GetFavoritesUseCase
import com.example.cofee_shop.domain.usecases.favourites.IsFavoriteUseCase
import com.example.cofee_shop.domain.usecases.favourites.RemoveFavoriteUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrderByIdUseCase
import com.example.cofee_shop.domain.usecases.orders.GetOrdersUseCase
import com.example.cofee_shop.domain.usecases.orders.OrderUseCases
import com.example.cofee_shop.domain.usecases.orders.PlaceOrderUseCase
import com.example.cofee_shop.domain.usecases.user.GetUserNameUseCase
import com.example.cofee_shop.domain.usecases.user.SaveUserNameUseCase
import com.example.cofee_shop.domain.usecases.user.UserUseCases
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
    fun provideCoffeeUseCases(
        getHotCoffees: GetHotCoffeesUseCase,
        getIcedCoffees: GetIcedCoffeesUseCase,
        getCoffeeById: GetCoffeeByIdUseCase,
        searchCoffee: SearchCoffeeUseCase
    ): CoffeeUseCases {
        return CoffeeUseCases(
            getHotCoffees = getHotCoffees,
            getIcedCoffees = getIcedCoffees,
            getCoffeeById = getCoffeeById,
            searchCoffee = searchCoffee
        )
    }

    @Provides
    @Singleton
    fun provideFavoriteUseCases(
        isFavorite: IsFavoriteUseCase,
        addFavorite: AddFavoriteUseCase,
        removeFavorite: RemoveFavoriteUseCase,
        getFavorites: GetFavoritesUseCase
    ): FavoriteUseCases {
        return FavoriteUseCases(
            isFavorite = isFavorite,
            addFavorite = addFavorite,
            removeFavorite = removeFavorite,
            getFavorites = getFavorites
        )
    }

    @Provides
    @Singleton
    fun provideOrderUseCases(
        getOrderById: GetOrderByIdUseCase,
        placeOrder: PlaceOrderUseCase,
        getOrders: GetOrdersUseCase
    ): OrderUseCases {
        return OrderUseCases(
            getOrderById = getOrderById,
            placeOrder = placeOrder,
            getOrders = getOrders
        )
    }

    @Provides
    @Singleton
    fun provideUserUseCases(
        saveUserName: SaveUserNameUseCase,
        getUserName: GetUserNameUseCase
    ): UserUseCases {
        return UserUseCases(
            saveUserName = saveUserName,
            getUserName = getUserName
        )
    }
}