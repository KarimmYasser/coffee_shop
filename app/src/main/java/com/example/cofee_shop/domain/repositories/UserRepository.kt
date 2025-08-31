package com.example.cofee_shop.domain.repositories

interface UserRepository {
    suspend fun saveUser(user: String)
    suspend fun getUser(): String?
    suspend fun isFirstLaunch(): Boolean
    suspend fun clearUserData()
}