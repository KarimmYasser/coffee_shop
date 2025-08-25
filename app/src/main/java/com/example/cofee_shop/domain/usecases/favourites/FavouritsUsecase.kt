package com.example.cofee_shop.domain.usecases.favourites

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class FavoriteUseCases @Inject constructor(
    val isFavorite: IsFavoriteUseCase,
    val addFavorite: AddFavoriteUseCase,
    val removeFavorite: RemoveFavoriteUseCase,
val getFavorites: GetFavoritesUseCase)