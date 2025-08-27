package com.example.cofee_shop.domain.usecases.favourites

import com.example.cofee_shop.domain.repositories.FavoriteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveFavoriteUseCase @Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend operator fun invoke(productId: Int) = favoriteRepository.removeFavorite(productId)
}