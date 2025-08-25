package com.example.cofee_shop.domain.usecases.user

import com.example.cofee_shop.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveUserNameUseCase @Inject constructor(userRepository: UserRepository) {
}