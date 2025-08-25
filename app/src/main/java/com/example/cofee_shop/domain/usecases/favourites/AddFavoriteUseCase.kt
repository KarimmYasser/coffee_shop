package com.example.cofee_shop.domain.usecases.favourites

import com.example.cofee_shop.domain.repositories.FavoriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddFavoriteUseCase @Inject constructor(FavoriteRepository: FavoriteRepository) {
}