package com.example.cofee_shop.domain.usecases.user

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class UserUseCases @Inject constructor(
    val saveUserName: SaveUserNameUseCase,
    val getUserName: GetUserNameUseCase,
)