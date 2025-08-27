package com.example.cofee_shop.data.repositories

import com.example.cofee_shop.data.local.prefrences.UserPreferences
import com.example.cofee_shop.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userPreferences: UserPreferences
) : UserRepository {

    override suspend fun saveUser(user: String) {
        userPreferences.saveUsername(user)
    }

    override suspend fun getUser(): String? {
        return userPreferences.getUsername()
    }

    override suspend fun isFirstLaunch(): Boolean {
        return userPreferences.isFirstLaunch()
    }

    override suspend fun clearUserData() {
        userPreferences.clearUserData()
    }
}