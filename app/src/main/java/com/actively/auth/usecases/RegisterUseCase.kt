package com.actively.auth.usecases

import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.repository.AuthRepository
import io.ktor.client.plugins.ClientRequestException

interface RegisterUseCase {

    suspend operator fun invoke(credentials: Credentials.Register): AuthResult
}

class RegisterUseCaseImpl(private val authRepository: AuthRepository) : RegisterUseCase {

    override suspend fun invoke(credentials: Credentials.Register) = try {
        val bearerTokens = authRepository.register(credentials)
        authRepository.setBearerTokens(bearerTokens)
        AuthResult.Success
    } catch (e: ClientRequestException) {
        e.printStackTrace()
        AuthResult.AccountExists
    } catch (e: Exception) {
        e.printStackTrace()
        AuthResult.Error
    }
}
