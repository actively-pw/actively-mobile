package com.actively.auth.usecases

import com.actively.repository.AuthRepository

interface LogOutUseCase {

    suspend operator fun invoke()
}

class LogOutUseCaseImpl(private val authRepository: AuthRepository) : LogOutUseCase {

    override suspend fun invoke() = authRepository.logout()
}
