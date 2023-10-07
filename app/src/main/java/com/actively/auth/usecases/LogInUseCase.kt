package com.actively.auth.usecases

import com.actively.auth.AuthResult
import com.actively.auth.Credentials
import com.actively.repository.AuthRepository
import io.ktor.client.plugins.ClientRequestException

interface LogInUseCase {

    suspend operator fun invoke(credentials: Credentials.Login): AuthResult
}

class LogInUseCaseImpl(private val authRepository: AuthRepository) : LogInUseCase {

    override suspend fun invoke(credentials: Credentials.Login) = try {
        val bearerTokens = authRepository.login(credentials)
        authRepository.setBearerTokens(bearerTokens)
        AuthResult.Success
    } catch (e: ClientRequestException) {
        e.printStackTrace()
        AuthResult.InvalidCredentials
    } catch (e: Exception) {
        e.printStackTrace()
        AuthResult.Error
    }
}

