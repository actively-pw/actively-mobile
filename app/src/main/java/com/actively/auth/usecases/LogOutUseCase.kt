package com.actively.auth.usecases

import com.actively.http.client.AuthorizedKtorClient
import com.actively.repository.AuthRepository

interface LogOutUseCase {

    suspend operator fun invoke()
}

class LogOutUseCaseImpl(
    private val authRepository: AuthRepository,
    private val client: AuthorizedKtorClient
) : LogOutUseCase {

    override suspend fun invoke() {
        authRepository.logout()
        client.clearCachedTokens()
    }
}
