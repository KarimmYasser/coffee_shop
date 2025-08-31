package com.example.cofee_shop.domain.usecases.user

import com.example.cofee_shop.domain.repositories.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserNameUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(): String? {
        return userRepository.getUser()
    }

}