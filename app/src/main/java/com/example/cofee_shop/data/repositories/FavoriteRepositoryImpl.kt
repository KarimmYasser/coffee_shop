package com.example.cofee_shop.data.repositories

import com.example.cofee_shop.data.local.database.dao.FavoriteDao
import com.example.cofee_shop.data.local.database.entities.FavoriteEntity
import com.example.cofee_shop.domain.repositories.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override suspend fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun isFavorite(drinkId: Int): Boolean {
        return favoriteDao.isFavorite(drinkId)
    }

    override suspend fun addFavorite(favorite: FavoriteEntity) {
        favoriteDao.addFavorite(favorite)
    }

    override suspend fun removeFavorite(drinkId: Int) {
        favoriteDao.removeFavorite(drinkId)
    }

    override suspend fun getFavoriteIds(): Flow<List<Int>> {
        return favoriteDao.getFavoriteIds()
    }
}